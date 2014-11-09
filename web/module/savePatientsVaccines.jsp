<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>
<%-- <%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%> --%>
<openmrs:htmlInclude
	file="/moduleResources/@MODULE_ID@/css/historyinterface.css" />
<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />

<table border="1" class="box" width="20%">
	<b class="boxHeader"><spring:message
		code="ezvacimmunization.savedPatientsImmunizationRecommendation" /></b>
	<div class="box">${encounterListSize} Encounter(s) saved</div>
</table>
<div class="box"><b><a
	href="../ezvacimmunization/schedulePatientImm.form">Back to Save
immunization</a> </b></div>

</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>