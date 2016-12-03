<%--
  Created by IntelliJ IDEA.
  User: YEEQiang
  Date: 2016/11/27 0027
  Time: 23:39
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>webuploader组件上传</title>
    <link rel="stylesheet" type="text/css" href="/assets/css/webuploader.css"/>
    <script type="application/javascript" src="/assets/js/jquery.js"></script>
    <script type="application/javascript" src="/assets/js/webuploader.js"></script>
    <style type="text/css">
        #dragArea {
            width: 200px;
            height: 100px;
            border-color: red;
            border-style: dashed;
        }
    </style>
    <script type="application/javascript">
        $(function(){
            // 获取文件的唯一标识
            var fileMd5;
            // 监听分块上传过程
            WebUploader.Uploader.register({
                "before-send-file" : "beforeSendFile",
                "before-send" : "beforeSend",
                "after-send-file" : "afterSendFile"
            },{
                // 时间点1：所有分块进行不上传之前调用此函数
                beforeSendFile : function(file) {
                    var deferred = WebUploader.Deferred();
                    // 1.计算文件的唯一标识,用于断点续传和秒传
                    (new WebUploader.Uploader()).md5File(file,0,5*1024*1024)
                            .progress(function(percentage) {
                                $("#"+file.id).find("div.state").text("正在获取文件信息...");
                            })
                            .then(function(val) {
                                fileMd5 = val;
                                $("#"+file.id).find("div.state").text("成功获取文件信息...");
                                // 只有文件信息获取成功,才进行下一步操作
                                deferred.resolve();
                            });
                    // 2.请求后台是否保存过该文件,如果存在,则跳过该文件,实现秒传功能
                    return deferred.promise();
                },
                // 时间点2：如果有分块上传,则每个分块上传之前调用此函数
                beforeSend : function(block) {
                    var deferred = WebUploader.Deferred();
                    // 1.请求后台是否保存过当前分块,如果存在,则跳过该分块文件，实现断点续传功能
                    console.debug(fileMd5);
                    $.ajax({
                        url:"${pageContext.request.contextPath}/checkChunk",
                        type:"POST",
                        data:{
                            "fileMd5" : fileMd5,
                            "chunk" : block.chunk,
                            "chunkSize" : block.end-block.start
                        },
                        dataType:"json",
                        success:function(responce) {
                            if (responce.ifExist) {
                                // 分块存在,跳过该分块
                                deferred.reject();
                            } else {
                                deferred.resolve();
                            }
                        }
                    });
                    this.owner.options.formData.fileMd5 = fileMd5;
                    return deferred.promise();
                },
                // 时间点3：所有分块上传成功之后调用此函数
                afterSendFile : function(file) {
                    console.debug(file);
                    // 1.如果分块上传,则通过后台合并所有分块文件
                    $.ajax({
                        url:"${pageContext.request.contextPath}/mergeChunkFile",
                        type:"POST",
                        data:{
                            "fileMd5" : fileMd5,
                            "fileName" : file.name
                        },
                        success:function(responce) {

                        }
                    });
                }
            });
            var uploader = WebUploader.create({
                // flash 控件地址
                swf : "/assets/js/Uploader.swf",
                // 后台提交地址
                server : "${pageContext.request.contextPath}/fileUpload",
                // 提交请求方式,默认为POST
                method : "POST",
                // 选择文件控件的标签
                pick : "#filePicker",
                // 自动上传文件
                auto : true,
                // 开启拖拽功能,指定拖拽区域
                dnd : "#dragArea",
                // 禁用页面其他地方的拖拽功能,防止页面直接打开文件
                disableGlobalDnd : true,
                // 开启黏贴功能
                paste : "#uploader",
                // 开启分块上传
                chunked : true,
                // 每块文件大小(默认为5M)
                chunkSize : 5*1024*1024,
                // {Arroy} [可选] [默认值：null] 指定接受哪些类型的文件
                accept : {
                    // {String} 文字描述
                    title : 'Images',
                    //  {String} 允许的文件后缀，不带点，多个用逗号分割。
                    extensions : 'gif,jpg,jpeg,bmp,png,pdf',
                    // {String} 多个用逗号分割
                    mimeTypes : 'images/*'
                }
            });

            uploader.on("fileQueued", function(file) {
                $("#fileList").append("<div id="+file.id+"><img/><span>"+file.name+"</span>" +
                        "<div><span class='percentage'></span></div>" +
                        "<div><span class='state'></span></div>" +
                        "</div>");
                uploader.makeThumb(file, function(error,src) {
                    if (error) {
                        $("#"+file.id).find("img").replaceWith("无法预览");
                    }
                    $("#"+file.id).find("img").attr("src", src);
                    console.debug(src);
                })
            });

            uploader.on("uploadProgress", function(file, percentage) {
                $("#"+file.id).find("span.percentage").text(Math.round(percentage * 100)+"%");
            });
        });
    </script>
</head>
<body>
    <div id="uploader">
        <%-- 用于拖拽文件 --%>
        <div id="dragArea"></div>
        <%-- 用于显示文件列表 --%>
        <div id="fileList"></div>
        <%-- 用于选择文件 --%>
        <div id="filePicker">选择文件</div>
    </div>
</body>
</html>
