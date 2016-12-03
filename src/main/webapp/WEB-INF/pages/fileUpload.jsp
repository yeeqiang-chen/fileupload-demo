<%--
  Created by IntelliJ IDEA.
  User: YEEQiang
  Date: 2016/11/26 0026
  Time: 16:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
    <title>WebUploader演示 - 带裁剪功能 </title>
    <link rel="stylesheet" type="text/css" href="/assets/css/webuploader.css"/>
    <link rel="stylesheet" type="text/css" href="/assets/css/style.css"/>
    <script type="application/javascript" src="/assets/js/jquery.js"></script>
    <script type="application/javascript" src="/assets/js/webuploader.js"></script>
    <script type="application/javascript" src="/assets/js/uploader.js"></script>
</head>
<body>
<div id="wrapper">
    <div class="uploader-container">
        <div id="filePicker">选择文件</div>
    </div>
    <!-- Croper container -->
    <div class="cropper-wraper webuploader-element-invisible">
        <div class="img-container">
            <img src="" alt=""/>
        </div>
        <div class="upload-btn">上传所选区域</div>
        <div class="img-preview"></div>
    </div>
</div>
</body>
</html>
