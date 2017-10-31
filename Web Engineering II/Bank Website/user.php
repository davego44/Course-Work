<?php
	require("dbConnect.php");
	require("session.php");
	
	$statement = $db->prepare("SELECT email, phone, address FROM project.user WHERE id = :user_id");
	$statement->execute(array(':user_id' => $_SESSION['user']));
	$user = $statement->fetch(PDO::FETCH_ASSOC);
	
	$email = $user['email'];
	$phone = $user['phone'];
	$address = $user['address'];
	$emailError = $phoneError = $addressError = $changesSaved = "";
	
	if ($_SERVER["REQUEST_METHOD"] == "POST") {
		if($_POST['type'] == "logout") {
			unset($_SESSION['user']);
			header("location: login.php");
		} else if($_POST['type'] == "transfer") {
			if (empty($_POST['amount'])) {
				$message = "You must enter an amount in to transfer.";
				echo "<script type='text/javascript'>alert('$message');</script>";
			} else {
				$amount = test_input($_POST['amount']);
				if ($_POST['fromAcc'] == $_POST['toAcc']) {
					$message = "You cannot transfer from and to the same account.";
					echo "<script type='text/javascript'>alert('$message');</script>";
				} else {
					$accSt->execute(array(':account_id' => $_POST['fromAcc']));
					$fromAcc = $accSt->fetch(PDO::FETCH_ASSOC);
					if ($fromAcc['amount'] - $_POST['amount'] < 0) {
						$message = "There is not enough money in the account to transfer.";
						echo "<script type='text/javascript'>alert('$message');</script>";
					} else {
						try {
							$db->beginTransaction();
							$updateSt = $db->prepare("UPDATE project.account SET amount = (amount - :amount) WHERE id = :account_id");
							$updateSt->execute(array(':amount' => $amount, ':account_id' => $_POST['fromAcc']));
							$updateSt = $db->prepare("UPDATE project.account SET amount = (amount + :amount) WHERE id = :account_id");
							$updateSt->execute(array(':amount' => $amount, ':account_id' => $_POST['toAcc']));
							$updateSt = $db->prepare("INSERT INTO project.history_entry (type_id, info, time, amount)
														VALUES((SELECT id FROM project.history_type WHERE name = 'Charge'), 'Transaction', current_timestamp, :amount)");
							$updateSt->execute(array(':amount' => $amount));
							$id = (int)$db->lastInsertId();
							$updateSt = $db->prepare("INSERT INTO project.history (account_id, history_entry_id) VALUES(:account_id, $id)");
							$updateSt->execute(array(':account_id' => $_POST['fromAcc']));
							$updateSt = $db->prepare("INSERT INTO project.history_entry (type_id, info, time, amount)
														VALUES((SELECT id FROM project.history_type WHERE name = 'Credit'), 'Transaction', current_timestamp, :amount)");
							$updateSt->execute(array(':amount' => $amount));
							$id = (int)$db->lastInsertId();
							$updateSt = $db->prepare("INSERT INTO project.history (account_id, history_entry_id) VALUES(:account_id, $id)");
							$updateSt->execute(array(':account_id' => $_POST['toAcc']));
							$db->commit();
						} catch (\PDOException $e) {
							$db->rollBack();
							echo $e;
						}
					}
				}
			}
		} else {
			if (empty($_POST["email"])) {
				$emailError = "Email is required";
			} else {
				$email = test_input($_POST["email"]);
			}
			if (empty($_POST["phone"])) {
				$phoneError = "Phone is required";
			} else {
				$phone = test_input($_POST["phone"]);
			}
			if (empty($_POST["address"])) {
				$addressError = "Address is required";
			} else {
				$address = test_input($_POST["address"]);
			}
			if (!empty($_POST["email"]) && 
				!empty($_POST["phone"]) && 
				!empty($_POST["address"])) {
					try {
						$db->beginTransaction();
						$updateSt = $db->prepare("UPDATE project.user SET email = :email, phone = :phone, address = :address WHERE id = :user_id");
						$updateSt->execute(array(':email' => $email, ':phone' => $phone, ':address' => $address, ':user_id' => $_SESSION['user']));
						$db->commit();
						$changesSaved = "Changes have been saved.";
					} catch (\PDOException $e) {
						$db->rollBack();
						$changesSaved = "Error occurred. Unable to save changes.";
					}
			}
		}
	}
	
	function test_input($data) {
		$data = trim($data);
		$data = stripslashes($data);
		$data = htmlspecialchars($data);
		return $data;
	}
?>
<!DOCTYPE html>
<html>
	<head>
		<title>David Banking Personal Info</title>
		<link rel="stylesheet" type="text/css" href="format.css">
	</head>
	<body>
		<!-- TRANSFER POPUP -->
		<div id="transferWindow" class="popUpWindow">
			<div class="transferContent">
				<span class="transferClose" onclick="CloseTransferWindow()">&times;</span>
				<h2>Transfer</h2><br/>
				<form action="" method="POST" class="form-inline">
					<div class="form-group">
						<label for="fromAcc" style="margin-right: 4px;">From</label>
						<select name="fromAcc" class="form-control" id="fromAcc" onChange="checkAcc();">
							<?php
								$myAccSt->execute(array(':user_id' => $_SESSION['user']));
								while ($account = $myAccSt->fetch(PDO::FETCH_ASSOC))
								{							
									echo '<option value="' . $account['account_id'] . '">' . $account['number'] . ' : ' . $account['name'] . '</option>';
									
								}
								$statement = $db->prepare("SELECT member_id FROM project.user_member WHERE user_id=$_SESSION[user]");
								$fmAccSt->execute(array(':user_id' => $_SESSION['user']));
								while ($account = $fmAccSt->fetch(PDO::FETCH_ASSOC))
								{					
									echo '<option value="' . $account['account_id'] . '">' . $account['number'] . ' : ' . $account['name'] . '</option>';
								}
							?>
						</select>
					</div>
					<div class="form-group">
						<label for="toAcc" style="margin: 0px 5px;">To</label>
						<select name="toAcc" class="form-control" id="toAcc" onChange="checkAcc();">
							<?php
								$myAccSt->execute(array(':user_id' => $_SESSION['user']));
								while ($account = $myAccSt->fetch(PDO::FETCH_ASSOC))
								{							
									echo '<option value="' . $account['account_id'] . '">' . $account['number'] . ' : ' . $account['name'] . '</option>';
									
								}
								$fmAccSt->execute(array(':user_id' => $_SESSION['user']));
								while ($account = $fmAccSt->fetch(PDO::FETCH_ASSOC))
								{					
									echo '<option value="' . $account['account_id'] . '">' . $account['number'] . ' : ' . $account['name'] . '</option>';
								}
							?>
						</select>
					</div><br/>
					<small id="accountError" class="form-text text-muted" style="color:red;"></small>
					<br/>
					<div class="form-group">
						<label for="amount">Amount: $</label>
						<input type="text" name="amount" onkeyup="checkAmount();" pattern="[0-9]*\.[0-9]{2}" title="Can contain only numbers and a decimal. There must be a decimal followed by two numbers." class="form-control" aria-describedby="amountError" id="amount"/>
						<br/><small id="amountError" class="form-text text-muted" style="color:red;"></small>
					</div><br/><br/>
					<input type="button" onclick="CloseTransferWindow()" value="Cancel" class="btn btn-primary"/>
					<input type="hidden" name="account_id" value="<?php echo $_POST['account_id'];?>"/>
					<input type="hidden" name="type" value="transfer"/>
					<input type="submit" value="Confirm Transfer" class="btn btn-primary" style="margin-left:20px;"/>
				</form>
			</div>
		</div>
		<!-- BEGIN PAGE -->
		<h1 style="padding: 5px; margin: 5px;">David Banking</h1>
		<nav class="navbar navbar-default" style="margin-bottom: 5px;">
			<div style="margin: 0 10%;">
				<ul class="nav navbar-nav">
					<li><a href="home.php">Accounts</a></li>
					<li class="dropdown active">
						<a class="dropdown-toggle" data-toggle="dropdown" href="#">
							User<span class="caret"></span></a>
						<ul class="dropdown-menu">
							<li><a href="user.php">Personal Info</a></li>
						</ul>
					</li>
					<li><a onclick="ShowTransferWindow()" style="cursor: pointer;">Make Transfer</a></li>
					<li><a href="#">Other</a></li>
				</ul>
				<ul class="nav navbar-nav navbar-right">
					<li>
						<form action="" method="POST" style="margin-top: 10px;">
							<input type="hidden" name="type" value="logout"/>
							<input type="submit" class="btn btn-link" style="text-decoration: none;" value="Logout"/>
						</form>
					</li>
				</ul>
			</div>
		</nav>
		<div style="margin: 0 10%;">
			<h3>Edit Personal Information</h3>
			<form action="" method="POST">
				<div class="form-group">
					<label for="email">Email</label>
					<input type="email" name="email" maxlength="100" value="<?php echo $email;?>" class="form-control" aria-describedby="emailError"/>
					<small id="emailError" class="form-text text-muted" style="color:red;"><?php echo $emailError;?></small>
				</div>
				<div class="form-group">
					<label for="phone">Phone</label>
					<input type="tel" name="phone" value="<?php echo $phone;?>" class="form-control" aria-describedby="phoneError"/>
					<small id="phoneError" class="form-text text-muted" style="color:red;"><?php echo $phoneError;?></small>
				</div>
				<div class="form-group">
					<label for="address">Address</label>
					<input type="text" name="address" value="<?php echo $address;?>" class="form-control" aria-describedby="addressError"/>
					<small id="addressError" class="form-text text-muted" style="color:red;"><?php echo $addressError;?></small>
				</div>
				<input type="submit" value="Save" aria-describedby="changes" class="btn btn-primary"/><br/>
				<small id="changes" class="form-text text-muted" style="color:green;"><?php echo $changesSaved;?></small>
			</form>
		</div>

		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
		<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
		
		<link rel="stylesheet" type="text/css" href="transfer.css">
		<script src="transfer.js"></script>
	</body>
</html>