set /p profile= 
if "%profile%"=="product" (mvn clean package -P%profile% -Dmaven.test.skip=true) else (mvn clean package -P%profile%) 
pause