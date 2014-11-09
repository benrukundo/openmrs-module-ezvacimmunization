<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>
<openmrs:htmlInclude
	file="/moduleResources/@MODULE_ID@/css/historyinterface.css" />



<b class="boxHeader"><spring:message
	code="ezvacimmunization.savePatientsGroupImmunizationRecommendation" /></b>
<div class="box">
<p><b>List of current encounter type found in database</b>
<table border="1" class="box" width="50%">

	<tr>
		<th>Encounter Type ID</th>
		<th>Encounter Type Name</th>

	</tr>

	<c:forEach items="${encounterType}" var="encounterType">
		<tr>

			<td>${encounterType.encounterTypeId}</td>
			<td>${encounterType.name}</td>

		</tr>
	</c:forEach>
</table>
</div>

<b class="boxHeader">Set Encounter Type ID </b>
<div class="box"><openmrs:portlet url="globalProperties"
	parameters="title=${title}|propertyPrefix=ezvacimmunization.encountertypeid|readOnly=false" />
</div>


<h1><a href="savePatientsVaccines.form">Save Immunization
recommendation for encounters</a></h1>

</div>


<p><%@ include file="/WEB-INF/template/footer.jsp"%>