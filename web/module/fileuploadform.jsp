<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>

<openmrs:htmlInclude file="/script/jquery/jquery-1.3.2.min.js" />
<script type="text/javascript">
	var $j = jQuery.noConflict(); 
</script>
<h2><spring:message code="ezvacimmunization.file"/></h2>
<form id="moduleAddForm" action="fileuploadform.form" method="post"
	enctype="multipart/form-data">
	<input type="file" name="uploadFile" size="30" /> 
	<input type="hidden" name="action" value="upload" /> 
	<input type="submit" value="Upload" /></form>
<%@ include file="/WEB-INF/template/footer.jsp"%>