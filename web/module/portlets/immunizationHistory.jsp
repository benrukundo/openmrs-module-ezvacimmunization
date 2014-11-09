<%@ taglib prefix="openmrs" uri="/WEB-INF/taglibs/openmrs.tld"%>
<%@ taglib prefix="openmrs_tag" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<openmrs:htmlInclude
	file="/moduleResources/@MODULE_ID@/css/historyinterface.css" />

<div class="boxHeader"><b><spring:message
	code="ezvacimmunization.results.title" /></b></div>
<div class="box">
<table cellspacing="0" cellpadding="2" width="98%" id="obs">
	<tr id="obsListingHeaderRow">
		<th class="obsConceptName">Series</th>
		<!--		<th class="obsValue">Immunization</th>-->
		<th class="obsAlerts">Status</th>
		<th class="obsCreator">Sequence</th>
		<th class="obsCreator">Next Due Date</th>
	</tr>
	<c:forEach var="r" items="${model.results}">
		<c:choose>
			<c:when test="${r.status == 'D'}">

				<tr class="due">

					<td><b>${r.seriesName }</b></td>
					<!--					<td><b>${r.vaccineName }</b></td>-->
					<td><b>Due</b></td>
					<td><b>${r.nextShot}</b></td>
					<td><b><openmrs:formatDate date="${r.nextShotDate}"
						type="small" /></b></td>
				</tr>

			</c:when>

			<c:when test="${r.status == 'NA'}">
				<tr>

					<td>${r.seriesName }</td>

					<td>Not applicable</td>
					<td></td>
					<td><openmrs:formatDate date="${r.nextShotDate}" type="small" /></td>

				</tr>
			</c:when>
			<c:when test="${r.status == 'Y'}">
				<tr class="updated">

					<td>${r.seriesName }</td>
					<!--					<td>${r.vaccineName }</td>-->
					<td>Up To Date</td>
					<td>${r.nextShot}</td>
					<td><openmrs:formatDate date="${r.nextShotDate}" type="small" /></td>

				</tr>
			</c:when>
			<c:when test="${r.status == 'C'}">
				<tr class="complete">

					<td>${r.seriesName }</td>
					<!--					<td>${r.vaccineName }</td>-->
					<td>Complete</td>
					<c:choose>
						<c:when test="${r.nextShot == -1 }">
							<td><b>-</b></td>
						</c:when>
						<c:otherwise>
							<td><b>${r.nextShot}</b></td>
						</c:otherwise>
					</c:choose>
					<td><b><openmrs:formatDate date="${r.nextShotDate}"
						type="small" /></b></td>

				</tr>
			</c:when>
			<c:when test="${r.status == 'N'}">
				<tr class="notupdated">
					<td><b>${r.seriesName }</b></td>
					<!--					<td><b>${r.vaccineName }</b></td>-->
					<td><b>Not Up to Date</b></td>
					<td><b>${r.nextShot}</b></td>
					<td><b><openmrs:formatDate date="${r.nextShotDate}"
						type="small" /></b></td>
				</tr>
			</c:when>
			<c:otherwise>
				--
			</c:otherwise>

		</c:choose>
	</c:forEach>
</table>
</div>
<div class="boxHeader"><b><spring:message
	code="ezvacimmunization.ObsPortlet.title" /></b></div>
<div class="box">
<table cellspacing="0" cellpadding="2" width="98%" id="obs">
	<tr id="obsListingHeaderRow">
		<th class="obsConceptName">Immunization</th>
		<th class="obsValue">Admin Date</th>
		<th class="obsAlerts">Sequence No.</th>
		<th class="obsCreator">Valid?</th>
	</tr>

	<c:forEach var="h" items="${model.history}">

		<c:forEach var="dto" items="${h}">
			<tr>
				<td><a
					href="admin/encounters/encounter.form?encounterId=${dto.encounterId}">${dto.vaccineName
				}</a></td>
				<td><openmrs:formatDate date="${dto.adminDate }" type="small" />
				</td>
				<td><c:choose>
					<c:when test="${dto.shotNumber < 0}">
					${(dto.shotNumber - dto.shotNumber) - (dto.shotNumber)}				
				</c:when>
					<c:when test="${dto.shotNumber != -1}">
					${dto.shotNumber}
					</c:when>

				</c:choose></td>
				<td><c:choose>

					<c:when test="${dto.valid == 'false' }">
													${model.no}
					</c:when>
					<c:when test="${dto.valid == 'true'}">
									${model.yes}
					</c:when>
				</c:choose></td>
			</tr>
		</c:forEach>
	</c:forEach>

</table>
</div>
<p><p>
<div class="boxHeader"><b><spring:message
	code="ezvacimmunization.title.immunizationRecommendation" /></b></div>
<div class="box"><b><a
	href="module/ezvacimmunization/immunizationEncounter.form?patientId=${model.patientId}">Save
Immunization recommendation</a> </b></div>


