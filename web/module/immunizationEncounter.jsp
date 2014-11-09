<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>

<openmrs:htmlInclude
	file="/moduleResources/@MODULE_ID@/css/historyinterface.css" />

<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />
<openmrs:htmlInclude file="/scripts/dojoConfig.js" />
<openmrs:htmlInclude file="/scripts/dojo/dojo.js" />


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


<spring:hasBindErrors name="encounter">
	<spring:message code="fix.error" />
	<br />
</spring:hasBindErrors>
<p>

<b class="boxHeader"><spring:message code="Encounter.summary" /></b>
<div class="box"><form:form id="encountImmId"
	commandName="encounter" method="post">
	<table cellpadding="3" cellspacing="0">
		<tr>
			<th><spring:message code="Encounter.patient" /></th>

			<td><spring:bind path="patient">
				<openmrs_tag:patientField formFieldName="${status.expression}"
					initialValue="${patientId}" />
			</spring:bind></td>

		</tr>
		<tr>
			<th><spring:message code="Encounter.provider" /></th>
			<td>
			
			<spring:bind path="provider">
				<openmrs_tag:personField formFieldName="${status.expression}"
					initialValue="${status.value}" roles="Provider"  />
					<c:if test="${status.errorMessage != ''}">
					<span class="error">${status.errorMessage}</span>
				</c:if>
			</spring:bind>
			
			</td>

		</tr>
		<tr>

			
				<th><spring:message code="Encounter.location" /></th>
				<td><spring:bind path="encounter.location">
					<openmrs_tag:locationField formFieldName="location"
						initialValue="${status.value}" />
					<c:if test="${status.errorMessage != ''}">
						<span class="error">${status.errorMessage}</span>
					</c:if>
				</spring:bind></td>

			</tr>
			<tr>

			
				<th><spring:message code="Encounter.datetime" /></th>
				<td><spring:bind path="encounterDatetime">
					<input type="text" name="${status.expression}"
						value="${defaultDate}" onClick="showCalendar(this)" />
				</spring:bind></td>


				
			</tr>
			<tr>
				<th><spring:message code="Encounter.type" /></th>
				<td><spring:bind path="encounterType">
					<c:choose>
						<c:when test="${encounter.encounterId == null}">
							<select name="encounterType">
								<c:forEach items="${encounterTypes}" var="type">
									<option value="${type.encounterTypeId}"
										<c:if test="${type.encounterTypeId == status.value}">selected</c:if>>${type.name}</option>
								</c:forEach>
							</select>
						</c:when>
						<c:otherwise>
							${encounter.encounterType.name}
						</c:otherwise>
					</c:choose>
				</spring:bind></td>
				<td><form:errors path="encounterType" cssClass="error" /></td>
			</tr>
	</table>



	<input type="submit" id="saveEncounterButton"
		value='<spring:message code="Encounter.save"/>'>
	&nbsp;
	<input type="button" value='<spring:message code="general.cancel"/>'
		onclick="history.go(-1); return; document.location='index.htm?autoJump=false&phrase=<request:parameter name="phrase"/>'">
</form:form></div>

<%@ include file="/WEB-INF/template/footer.jsp"%>