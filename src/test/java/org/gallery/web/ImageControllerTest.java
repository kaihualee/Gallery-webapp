package org.gallery.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.gallery.common.ThumbnailSize;
import org.gallery.persist.ImageDao;
import org.gallery.web.controller.ImageController;
import org.gallery.web.vo.ImageVO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:applicationContext-test.xml",
		"classpath:spring-servlet-test.xml" })
public class ImageControllerTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Autowired
	private ImageController controller;

	final String filename = "img-test/upload.jpg";
	final String sourcefilename = "img-test/source.jpg";
	final String matchfilename = "img-test/match.jpg";

	final String propfilename = "data-test/imagecontroller-test.properties";
	private Properties prop = new Properties();

	final ObjectMapper mapper = new ObjectMapper();

	private ClassLoader loader = Thread.currentThread().getContextClassLoader();

	@Before
	public void setup() throws IOException {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
				.alwaysExpect(status().isOk())
				.addFilters(new CharacterEncodingFilter()).build();
		prop.load(loader.getResourceAsStream(propfilename));
	}

	@Test
	@Transactional
	public void testUpload() throws UnsupportedEncodingException, Exception {
		uploadfileForTest(filename);
	}

	@Test
	@Transactional
	public void testThumbnails() throws Exception {
		ImageVO vo = uploadfileForTest(filename);
		for (ThumbnailSize thumbnail_Enum : ThumbnailSize.values()) {
			MockHttpServletResponse response = mockMvc
					.perform(
							get(vo.getThumbnailUrl()).param("size",
									Integer.toString(thumbnail_Enum.getId())))
					.andExpect(status().isOk()).andReturn().getResponse();
			String contenttype = response.getContentType();
			String fileformatname = contenttype.substring(
					contenttype.lastIndexOf("/") + 1, contenttype.length());

			URL resource = loader.getResource(filename);
			File file = new File(resource.getFile());
			String outpath = file.getParentFile().getCanonicalPath()
					+ File.separatorChar + UUID.randomUUID() + "."
					+ fileformatname;
			System.out.println("outpath: " + outpath);
			FileOutputStream out = new FileOutputStream(outpath);
			out.write(response.getContentAsByteArray());
			out.flush();
			out.close();
		}

	}

	@Test
	@Transactional
	public void testPicure() throws JsonParseException, JsonMappingException,
			UnsupportedEncodingException, IOException, Exception {
		ImageVO vo = uploadfileForTest(filename);
		MockHttpServletResponse response = mockMvc.perform(get(vo.getUrl()))
				.andExpect(status().isOk()).andReturn().getResponse();
		String contenttype = response.getContentType();
		String fileformatname = contenttype.substring(
				contenttype.lastIndexOf("/") + 1, contenttype.length());

		URL resource = loader.getResource(filename);
		File file = new File(resource.getFile());
		String outpath = file.getParentFile().getCanonicalPath()
				+ File.separatorChar + UUID.randomUUID() + "." + fileformatname;
		System.out.println("outpath: " + outpath);
		FileOutputStream out = new FileOutputStream(outpath);
		out.write(response.getContentAsByteArray());
		out.flush();
		out.close();
	}

	@Test
	@Transactional
	public void testDelete() throws IOException, Exception {
		String url;
		ImageVO vo = uploadfileForTest(filename);
		System.out.println(vo.getId());
		url = vo.getDeleteUrl();
		mockMvc.perform(
				delete(url)
						.accept(MediaType
								.parseMediaType("application/json;charset=UTF-8")))
				.andDo(print()).andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$[0].success").value(true));
	}

	@Test
	@Transactional
	public void testConvert() throws JsonParseException, JsonMappingException,
			UnsupportedEncodingException, IOException, Exception {
		// "id1", "id2", "option", "size"
		ImageVO srcVO = uploadfileForTest(sourcefilename);
		ImageVO matchVO = uploadfileForTest(matchfilename);
		String url = prop.getProperty("url_convert");
		Long option = 2L;
		for (ThumbnailSize thumbnail_Enum : ThumbnailSize.values()) {
			MockHttpServletResponse response = mockMvc
					.perform(
							get(url).param("id1", String.valueOf(srcVO.getId()))
									.param("id2",
											String.valueOf(matchVO.getId()))
									.param("option", String.valueOf(option))
									.param("size",
											String.valueOf(thumbnail_Enum
													.getId()))
									.accept(MediaType
											.parseMediaType("application/json;charset=UTF-8")))
					.andDo(print()).andExpect(status().isOk())
					.andExpect(jsonPath("$.filename").exists()).andReturn()
					.getResponse();
			Map<String, String> result = mapper.readValue(
					response.getContentAsString(),
					new TypeReference<Map<String, String>>() {
					});
			
			//download file
			String dwfilename = result.get("filename");
			response = mockMvc
					.perform(
							get("/download/{name:.+}", dwfilename).param(
									"attachment", "false"))
					.andExpect(status().isOk()).andReturn().getResponse();

			URL resource = loader.getResource(filename);
			File file = new File(resource.getFile());
			String outpath = file.getParentFile().getCanonicalPath()
					+ File.separatorChar + UUID.randomUUID() + dwfilename;
			System.out.println("outpath: " + outpath);
			FileOutputStream out = new FileOutputStream(outpath);
			out.write(response.getContentAsByteArray());
			out.flush();
			out.close();

		}
	}

	/**
	 * @return
	 * @throws Exception
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 */
	private ImageVO uploadfileForTest(String uploadfilename) throws Exception,
			IOException, UnsupportedEncodingException, JsonParseException,
			JsonMappingException {
		String url = prop.getProperty("url_upload");
		URL resource = loader.getResource(uploadfilename);
		File file = new File(resource.getFile());
		MockHttpServletResponse reponse = mockMvc
				.perform(
						fileUpload(url)
								.file(uploadfilename, getBytesFromFile(file))
								.contentType(MediaType.IMAGE_JPEG)
								.accept(MediaType
										.parseMediaType("application/json;charset=UTF-8")))
				.andDo(print()).andExpect(status().isOk())
				.andExpect(jsonPath("$.files").exists())
				.andExpect(jsonPath("$.files").isArray())
				.andExpect(jsonPath("$.files[0].id").exists())
				.andExpect(jsonPath("$.files[0].name").exists())
				.andExpect(jsonPath("$.files[0].size").exists())
				.andExpect(jsonPath("$.files[0].url").exists())
				.andExpect(jsonPath("$.files[0].thumbnailUrl").exists())
				.andExpect(jsonPath("$.files[0].deleteType").value("DELETE"))
				.andExpect(jsonPath("$.files[0].deleteUrl").exists())
				.andExpect(jsonPath("$.files[0].emotionUrl").exists())
				.andReturn().getResponse();
		Map<String, List<ImageVO>> result = mapper.readValue(
				reponse.getContentAsString(),
				new TypeReference<Map<String, List<ImageVO>>>() {
				});
		ImageVO vo = result.get("files").get(0);
		return vo;
	}

	public static byte[] getBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);
		// 获取文件大小
		long length = file.length();
		if (length > Integer.MAX_VALUE) {
			// 文件太大，无法读取
			throw new IOException("File is to large " + file.getName());

		}
		// 创建一个数据来保存文件数据
		byte[] bytes = new byte[(int) length];

		// 读取数据到byte数组中
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {

			offset += numRead;

		}

		// 确保所有数据均被读取
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file "
					+ file.getName());

		}
		// Close the input stream and return bytes
		is.close();
		return bytes;
	}

	class MockImageController extends ImageController {
		public void setImageDao(ImageDao imageDao) {
			this.imageDao = imageDao;
		}

		public void setFileUploadDirectory(String uploadDir) {
			this.fileUploadDirectory = uploadDir;
		}
	}
}
