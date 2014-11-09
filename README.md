== Description ==

The immunization module is an OpenMRS module which does immunization
decision-support.
The introduction of this module is to help doctors or nurses in immunization
decision-support. The use of the immunization module will help to
* Decrease rate of inappropriate vaccinations (given at the wrong time, given
* when already given before, or given when not supposed to be given)
* Decrease proportion of missed vaccinations (according to vaccination schedule)
* Decrease incidence of vaccine-preventable diseases like polio, tetanus in population

====Installation & Configuration==========

The omod file (ezvacimmunization.omod) is used to install the immunization
module in OpenMRS
This module requires two additional configuration files to be uploaded
  * The utdrules file that contains all vaccines rules that the ezvac module use to perform all vaccine's calculation.
  * Vaccinemap.csv file that contains a list of all vaccines with their cvx code that will be used in the ezvac module, this file can be edited by adding
vaccine and its cvxcode. The vaccine name and the cvxcode are separated by a comma (,) eg. VARICELLA,94,21.

Details are in the immunization documentation.





