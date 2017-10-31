var transferWindow = document.getElementById("transferWindow");

function ShowTransferWindow() {
	transferWindow.style.display = "block";
}

function CloseTransferWindow() {
	transferWindow.style.display = "none";
}

window.onclick = function(event) {
	if (event.target == transferWindow) {
		transferWindow.style.display = "none";
	}
}

function checkAmount() {
	var amount = parseFloat($('#amount').val());
	if (amount != amount) {
		$('#amountError').html("Please enter a valid amount.");
	} else if (amount <= 0) {
		$('#amountError').html("Amount must be greater than 0.");
	} else {
		$('#amountError').html("");
	}
}
			
function checkAcc() {
	var fromAcc = $('#fromAcc').val();
	var toAcc = $('#toAcc').val();
	if (fromAcc == toAcc) {
		$('#accountError').html("You cannot transfer from and to the same account.");
	} else {
		$('#accountError').html("");
	}
}