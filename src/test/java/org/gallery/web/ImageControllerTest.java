package org.gallery.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.easymock.EasyMock;
import org.gallery.common.ThumbnailSize;
import org.gallery.web.controller.ImageController;
import org.gallery.web.vo.ImageVO;
import org.hibernate.sql.Delete;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

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

	final String outfilename = "thumbnails";

	final ObjectMapper mapper = new ObjectMapper();
	

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
				.alwaysExpect(status().isOk())
				.addFilters(new CharacterEncodingFilter()).build();
	}

	@Test
	public void testUpload() throws UnsupportedEncodingException, Exception {
		String url = "/upload";
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL resource = loader.getResource(filename);
		File file = new File(resource.getFile());
		mockMvc.perform(
				fileUpload(url)
						.file(filename, getBytesFromFile(file))
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
				.andExpect(jsonPath("$.files[0].emotionUrl").exists());
	}

	@Test
	public void testThumbnails() throws Exception {
		String url = "/thumbnail/{id}";
		String id = "2";
		MockHttpServletResponse response = mockMvc
				.perform(
						get(url, id)
								.param("size",
										Integer.toString(ThumbnailSize.MEDIUM_SIZE
												.getId()))
								.accept(MediaType
										.parseMediaType("application/json;charset=UTF-8")))
				.andExpect(status().isOk()).andReturn().getResponse();
		String contenttype = response.getContentType();
		String fileformatname = contenttype.substring(
				contenttype.lastIndexOf("/") + 1, contenttype.length());

		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL resource = loader.getResource(filename);
		File file = new File(resource.getFile());
		String outpath = file.getParentFile().getCanonicalPath()
				+ File.separatorChar + outfilename + "." + fileformatname;
		System.out.println("outpath: " + outpath);
		FileOutputStream out = new FileOutputStream(outpath);
		out.write(response.getContentAsByteArray());
		out.flush();
		out.close();

	}

	@Test
	@Transactional
	public void testDelete() throws IOException, Exception {
		String url = "/upload";
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL resource = loader.getResource(filename);
		File file = new File(resource.getFile());
		MockHttpServletResponse reponse = mockMvc
				.perform(
						fileUpload(url)
								.file(filename, getBytesFromFile(file))
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
				reponse.getContentAsString(), Map.class);
		List<ImageVO> list = (List<ImageVO>) result.get("files");

		ImageVO vo = list.get(0);
		Long id = vo.getId();
		System.out.println(id);
		url = "/delete/{id}";
		mockMvc.perform(
				delete(url, id)
						.accept(MediaType
								.parseMediaType("application/json;charset=UTF-8")))
				.andDo(print()).andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$[0].success").value(true));
	}

	@Test
	public void testConvert() {
		// using mockobject
		// accountDao = easyMock.createMock(AccountDao.class) ;
		// accountService = new AccountServiceImpl(accountDao);
		//
		// Account account = new Account(TEST_ACCOUNT_NO, 100);
		// accountDao.findAccount(TEST_ACCOUNT_NO);
		// easyMock.expectLastCall().andReturn(account);
		// account.setBalance(150);
		// accountDao.updateAccount(account);
		// easyMock.replay();
		// accountService.deposit(TEST_ACCOUNT_NO, 50);
		// easyMock.verify();

		ThumbnailSize Thumbnail_Enum = ThumbnailSize.MEDIUM_SIZE;
		int srcId = 5;
		int destId = 6;
		int option = 1;
		controller.convert(srcId, destId, option, Thumbnail_Enum.getId());
	}

	@Test
	public void testPicure() {
		Long id = 12L;
		// controller.picture(null, id);
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

}
