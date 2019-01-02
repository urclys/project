/**
 * 
 */
function selectAllBoursierAdmis(source) {
	checkboxes = document.getElementsByName('checkbox1');
	for ( var i in checkboxes)
		checkboxes[i].checked = source.checked;
}
function selectAllBoursierEnAttente(source) {
	checkboxes = document.getElementsByName('checkbox2');
	for ( var i in checkboxes)
		checkboxes[i].checked = source.checked;
}