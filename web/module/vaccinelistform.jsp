<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>
<%-- <%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%> --%>
<openmrs:htmlInclude
	file="/moduleResources/@MODULE_ID@/css/historyinterface.css" />
<openmrs:htmlInclude file="/script/jquery/jquery-1.3.2.min.js" />
<script type="text/javascript">
	var $j = jQuery.noConflict(); 
</script>
<h2><spring:message code="ezvacimmunization.listofvaccine" /></h2>
<div class="box"><b>Vaccines list of immunization module</b>
<p></p>
<table border="1">

	<tr>

	</tr>
	<tr>

		<td>HEPB</td><td>HEPATITIS B VACCINES</td>
		</tr>
		<tr>
		<td>TDAP</td><td>TDAP</td>
		</tr>
		<tr>
		<td>TD</td><td>TD</td>
		</tr>
		<tr>
		<td>DTAP</td><td>DIPHTHERIA TETANUS AND PERTUSSIS VACCINATION</td>
		</tr>
		<tr>
		<td>IPV</td><td>POLIO VACCINATION, INACTIVATED</td>
		
		</tr>
		<tr>
		<td>ROTAVIRUS</td><td>ROTAVIRUS</td>
		</tr>
		<tr>
		<td>HIB</td><td>HIB</td>
		</tr>
		<tr>
		<td>VARICELLA</td><td>VARICELLA</td>
		</tr>
		<tr>
		<td>MMR</td><td>Measles,mumps and rubella virus vaccine </td>
		</tr>
		<tr>
		<td>HEPA</td><td>HEPATITIS A VACCINATION</td>
		</tr>
		<tr>
		<td>HPV</td><td>HUMAN PAPILLOMAVIRUS</td>
		</tr>
		<tr>
		<td>MENACTRA</td><td>MENACTRA</td>
		</tr>
		<tr>
		<td>INFLUENZA</td><td>INFLUENZA</td>
		</tr>
		<tr>
		<td>ROTARIX</td><td>ROTARIX</td>
		</tr>
		<tr>
		<td>PEDVAX</td><td>PEDVAX</td>
		</tr>
		<tr>
		<td>RECOMBIVAX</td><td>RECOMBIVAX</td>
		</tr>
		<tr>
		<td>BCG</td><td>BACILLE CAMILE-GUERIN VACCINATION</td>
		</tr>		
	
</table>

<p>
<i>From the list of vaccines above, please specify below in capital letter the vaccines to be used, vaccines to be used are separated by a comma</i>
</p>
<openmrs:portlet url="globalProperties"
	parameters="title=${title}|propertyPrefix=ezvacimmunization.vaccinelist|readOnly=false" />
</div>
<%@ include file="/WEB-INF/template/footer.jsp"%>