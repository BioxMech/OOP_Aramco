# OOP_Aramco
 
You require maven to install the JAR File from the springboot backend application.
<br /><br />
<code>mvn -version</code>

## For default deployment:
1. Place AramcoApp folder on Desktop
2. Using command prompt or terminal, change directory to AramcoApp
3. Run command: <code>java -jar SpringBootMongoDBRestAPIs-1.00.jar</code>

## For customized deployment:
1. Navigate to the directory SpringBootMongoDB/src/main/resources using Windows Explorer or File Manager (on MacBook)
2. Open application.properties using NotePad to change the configurations to your own preferences
3. Navigate to the directory SpringBootMongoDB/ using command prompt
   - You should see the file pom.xml
4. Run command: <code>mvn clean install</code>
5. Navigate to SpringBootMongoDB/target
   - You should see SpringBootMongoDBRestAPIs-1.00.jar
6. Copy the file and paste in SpringBootMongoDB/
7. Run command: <code>java -jar SpringBootMongoDBRestAPIs-1.00.jar</code>


<br /> If an error appears, please follow this <a href="https://www.baeldung.com/install-maven-on-windows-linux-mac">Guide</a> to install Maven on your OS.
