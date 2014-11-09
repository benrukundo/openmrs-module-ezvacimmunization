<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>
<openmrs:htmlInclude
	file="/moduleResources/@MODULE_ID@/css/historyinterface.css" />

<h2><spring:message code="ezvacimmunization.listofvaccine" /></h2>

<div class="box">
<p><b>Vaccines and cvx code values retrieved from vacicnemap.csv
file uploaded</b>
<table border="1" class="box" width="50%">

	<tr>
		<th>Vaccines</th>
		<th>Cvx Code</th>
	</tr>

	<c:forEach items="${vaccinemap}" var="entry">
		<tr>
			<td>${entry.key}</td>
			<td><c:forEach items="${entry.value}" var="item"
				varStatus="loop">
				${item} ${!loop.last ? ', ' : ''}
			</c:forEach></td>
		</tr>
	</c:forEach>
</table>


<p><i> From the list of vaccines above, please specify below in
capital letter the vaccines to be used, vaccines to be used are
separated by a comma</i></p>
<p><i>Additional vaccine to be used can be added in
vaccinemap.csv file and then uploaded.</i>
<p><openmrs:portlet url="globalProperties"
	parameters="title=${title}|propertyPrefix=ezvacimmunization.vaccinelist|readOnly=false" />
</div>

</div>

<p><%@ include file="/WEB-INF/template/footer.jsp"%>