<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<body>
	<h1>${message}</h1>
	<form action="${pageContext.request.contextPath}/fileUpload"
			id="fileUploadForm" method="post" enctype="multipart/form-data">
		<input type="file" name="file">
		<input type="text" name="desc">
		<input type="submit" value="submit">
	</form>
</body>
<script>
	/*$(function(){
//		$("#fileUploadForm").submit
	})*/
</script>
</html>