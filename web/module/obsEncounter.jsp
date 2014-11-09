<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>
<%-- <%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%> --%>
<openmrs:htmlInclude
	file="/moduleResources/@MODULE_ID@/css/historyinterface.css" />

<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />
<openmrs:htmlInclude file="/scripts/dojoConfig.js" />
<openmrs:htmlInclude file="/scripts/dojo/dojo.js" />

<script type="text/javascript">
	dojo.addOnLoad( function() {
		toggleVisibility(document, "div", "description");
		toggleRowVisibilityForClass("obs", "voided", true);
		voidedClicked(document.getElementById("voided"));
	})

</script>


<script type="text/javascript">

	function mouseover(row, isDescription) {
		if (row.className.indexOf("searchHighlight") == -1) {
			row.className = "searchHighlight " + row.className;
			var other = getOtherRow(row, isDescription);
			other.className = "searchHighlight " + other.className;
		}
	}
	function mouseout(row, isDescription) {
		var c = row.className;
		row.className = c.substring(c.indexOf(" ") + 1, c.length);
		var other = getOtherRow(row, isDescription);
		c = other.className;
		other.className = c.substring(c.indexOf(" ") + 1, c.length);
	}
	function getOtherRow(row, isDescription) {
		if (isDescription == null) {
			var other = row.nextSibling;
			if (other.tagName == null)
				other = other.nextSibling;
		}
		else {
			var other = row.previousSibling;
			if (other.tagName == null)
				other = other.previousSibling;
		}
		return other;
	}
	function click(obsId) {
		document.location = "${pageContext.request.contextPath}/admin/observations/obs.form?obsId=" + obsId;
		return false;
	}
	
	function voidedClicked(input) {
		var reason = document.getElementById("voidReason");
		var voidedBy = document.getElementById("voidedBy");
		if (input) {
		if (input.checked) {
			reason.style.display = "";
			if (voidedBy)
				voidedBy.style.display = "";
		}
		else {
			reason.style.display = "none";
			if (voidedBy)
				voidedBy.style.display = "none";
		}
		}
	}
	
	function enableSaveButton(relType, id) {
		document.getElementById("saveEncounterButton").disabled = false;
	}

</script>

<style>
#table th {
	text-align: left;
}

td.fieldNumber {
	width: 5px;
	white-space: nowrap;
}
</style>

<a
	href="../../patientDashboard.form?patientId=${encounter.patient.patientId}"><spring:message
	code="patientDashboard.viewDashboard" /></a>

<h2><spring:message code="ezvacimmunization.title.immunizationRecommendation" /></h2>


<b class="boxHeader"><spring:message code="Encounter.summary" /></b>
<div class="box"><form:form id="obsForm" commandName="obs"
	method="post">
	<table cellpadding="3" cellspacing="0">
		<table>
			<tr>
				<td>Patient:</td>
				<td>${encounter.patient.names}</td>
			</tr>
			<tr>
				<td>Provider :</td>
				<td>${encounter.provider.names}</td>
			</tr>
			<tr>
				<td>Encounter Date :</td>
				<td>${encounter.encounterDatetime}</td>
			</tr>
			<tr>
				<td>Location :</td>
				<td>${encounter.location}</td>
			</tr>
			<tr>
				<td>Encounter Type:</td>
				<td>${encounter.encounterType.name}</td>
			</tr>

			<tr>



				<td>${immunizationRecommConcept}</td>
				<td><c:forEach var="vacrecom" items="${obs.valueText}">

					${vacrecom}
					</c:forEach>
			</td>




			</tr>
		</table>




		<input type="submit" id="saveObsButton"
			value='<spring:message code="Obs.save"/>'>
		&nbsp;
		<input type="button" value='<spring:message code="general.cancel"/>'
			onclick="history.go(-1); return; document.location='index.htm?autoJump=false&phrase=<request:parameter name="phrase"/>'">
		</form:form>
		</div>

		<%@ include file="/WEB-INF/template/footer.jsp"%>