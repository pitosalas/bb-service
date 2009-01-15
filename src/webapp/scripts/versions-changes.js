nextNum = 0;

function deleteChange(num) {
  changeBlockId = "change-" + num;
  changeBlock = document.getElementById(changeBlockId);
  if (changeBlock) {
    changeBlock.parentNode.removeChild(changeBlock);
  }
}

function createChangeNode(num, type, details) {
  if (!type) type = 0;

  changeDiv = document.createElement("div");
  changeDiv.setAttribute("id", "change-" + num);

  opsDiv = document.createElement("div");
  opsDiv.setAttribute("class", "ops");
  changeDiv.appendChild(opsDiv);

  deleteA = document.createElement("a");
  deleteA.setAttribute("href", "#");
  deleteA.setAttribute("onclick", "deleteChange('" + num + "'); return false;");
  deleteA.appendChild(document.createTextNode("Delete"));
  opsDiv.appendChild(deleteA);

  selectDiv = document.createElement("div");
  selectDiv.setAttribute("class", "type");
  changeDiv.appendChild(selectDiv);

  select = document.createElement("select");
  select.setAttribute("name", "type-" + num);
  selectDiv.appendChild(select);

  option0 = document.createElement("option");
  option0.setAttribute("value", "0");
  if (type == 0) option0.setAttribute("selected", "true");
  option0.appendChild(document.createTextNode("Feature"));

  option1 = document.createElement("option");
  option1.setAttribute("value", "1");
  if (type == 1) option1.setAttribute("selected", "true");
  option1.appendChild(document.createTextNode("Fix"));

  select.appendChild(option0);
  select.appendChild(option1);

  detailsDiv = document.createElement("div");
  changeDiv.appendChild(detailsDiv);

  detailsInput = document.createElement("input");
  detailsInput.setAttribute("class", "details");
  detailsInput.setAttribute("type", "text");
  detailsInput.setAttribute("name", "details-" + num);
  if (details) detailsInput.setAttribute("value", details);
  detailsDiv.appendChild(detailsInput);

  return changeDiv;
}

function addChange(type, details) {
  changesBlock = document.getElementById("changes");
  newChangeBlock = createChangeNode(nextNum++, type, details);
  changesBlock.appendChild(newChangeBlock);
}
