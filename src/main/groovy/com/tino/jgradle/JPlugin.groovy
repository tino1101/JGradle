package com.tino.jgradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class JPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.extensions.create('downloadFile', DownloadFile)
        project.extensions.create('createClass', CreateClass)
        project.task('downloadFile') {
            doLast {
                download(project.downloadFile.url, project.downloadFile.name)
            }
        }
        project.task('createClass') {
            doLast {
                createClass(project.createClass.packageName, project.createClass.className, project.createClass.property)
            }
        }
    }

    void download(String src, String name) throws IOException {
        String folderPath = System.getProperty("user.dir")
        URL url = new URL(src)
        def con = (HttpURLConnection) url.openConnection()
        InputStream is = con.getInputStream()
        OutputStream os = new BufferedOutputStream(new FileOutputStream(folderPath + "/" + name))
        int content;
        while ((content = is.read()) != -1) {
            os.write(content)
        }
        os.close()
        is.close()
        print 'download success：' + folderPath + "/" + name
    }

    void createClass(String dir, String name, String property) {
        String tempDir = dir
        if (tempDir.contains(".")) tempDir.replaceAll(".", "/")
        String dirPath = System.getProperty("user.dir") + "/src/main/java/" + tempDir
        File directory = new File(dirPath)
        if (!directory.exists()) directory.mkdirs()
        File file = new File(dirPath + "/" + name + ".java")
        if (!file.exists()) file.createNewFile()
        StringBuilder stringBuilder = new StringBuilder("package " + dir + ";")
        stringBuilder.append("\n\npublic class " + name + " {\n")
        String[] propertyArr = property.replaceAll(" ", "").split(",")
        for (int i = 0; i < propertyArr.length; i++) {
            stringBuilder.append("\n")
            if (i > 0) {
                stringBuilder.append("\n")
            }
            stringBuilder.append("\tpublic String " + propertyArr[i] + ";")
        }
        for (int i = 0; i < propertyArr.length; i++) {
            stringBuilder.append("\n\n")
            stringBuilder.append("\tpublic String get" + CharUtil.captureName(propertyArr[i]) + "() {\n\t\treturn " + propertyArr[i] + ";\n\t}")
            stringBuilder.append("\n\n")
            stringBuilder.append("\tpublic void set" + CharUtil.captureName(propertyArr[i])
                    + "(String " + propertyArr[i] + ") {\n\t\tthis." + propertyArr[i] + " = " + propertyArr[i] + ";\n\t}")
        }
        stringBuilder.append("\n\n}")
        byte[] bytes = stringBuilder.toString().getBytes()
        FileOutputStream outStream = new FileOutputStream(file)
        outStream.write(bytes)
        outStream.close()
        print 'generate class success：' + dirPath + "/" + name + ".java"
    }
}