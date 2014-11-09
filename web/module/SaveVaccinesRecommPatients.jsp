<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>

<openmrs:htmlInclude
	file="/moduleResources/@MODULE_ID@/css/historyinterface.css" />
<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />


<style>
#table th {
	text-align: left;
}

td.fieldNumber {
	width: 5px;
	white-space: nowrap;
}
</style>


<spring:hasBindErrors name="encounter">
	<spring:message code="fix.error" />
	<br />
</spring:hasBindErrors>


<b class="boxHeader">Please provides Encounter information</b>
<div class="box"><form:form id="encountImmId"
	commandName="encounter" method="post">
	<table cellpadding="3" cellspacing="0">

		<tr>
			<th><spring:message code="Encounter.provider" /></th>
			<td><spring:bind path="provider">
				<openmrs_tag:personField formFieldName="${status.expression}"
					initialValue="${status.value}" roles="Provider" />
				<c:if test="${status.errorMessage != ''}">
					<span class="error">${status.errorMessage}</span>
				</c:if>
			</spring:bind></td>

		</tr>

		<tr>


			<th><spring:message code="Encounter.datetime" /></th>
			<td><spring:bind path="encounterDatetime">
				<input type="text" name="${status.expression}"
					value="${status.value}" onClick="showCalendar(this)" />
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
</form:form>



</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>