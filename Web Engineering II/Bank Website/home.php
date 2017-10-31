<?php
	require("dbConnect.php");
	require("session.php");
	
	if($_SERVER["REQUEST_METHOD"] == "POST") {
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
		<title>David Banking</title>
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
		<nav class="navbar navbar-default">
			<div style="margin: 0 10%;">
				<ul class="nav navbar-nav">
					<li class="active"><a href="#">Accounts</a></li>
					<li class="dropdown">
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
		<div class="center-container" style="margin: 0 10%;">
			<?php 
				$statement = $db->prepare("SELECT first_name FROM project.user WHERE id=:user_id");
				$statement->execute(array(':user_id' => $_SESSION['user']));
				$user_name = $statement->fetch(PDO::FETCH_ASSOC);
				echo "<h3>Welcome $user_name[first_name]!</h3>";
			?>
			<br/>
			<h4>Your Accounts</h4>
			<table class="table table-hover">
				<thead>
					<tr>
						<th>Type</th>
						<th>Number</th>
						<th>Balance</th>
					</tr>
				</thead>
				<tbody>
					<?php
						$myAccSt->execute(array(':user_id' => $_SESSION['user']));
						while ($account = $myAccSt->fetch(PDO::FETCH_ASSOC))
						{
							echo '<tr onclick="postToAccountDetails(this)" style="cursor: pointer;" var="'. $account['account_id'] .'">';
							echo "<td>$account[name]</td>";
							echo "<td>$account[number]</td>";
							echo "<td>\$$account[amount]</td>";
							echo "</tr>";
						}
					?>
				</tbody>
			</table>
			<form action="account.php" method="POST" name="account-details" class="account-details">
				<input type="hidden" name="account_id" class="account_id" value=""/>
				<input type="submit" style="display: none;"/>
			</form>
			<br/>
			<h4>Family Member Accounts</h4>
			<table class="table table-hover">
				<thead>
					<tr>
						<th>Member</th>
						<th>Type</th>
						<th>Number</th>
						<th>Balance</th>
					</tr>
				</thead>
				<tbody>
					<?php
						$statement = $db->prepare("SELECT member_id FROM project.user_member WHERE user_id=$_SESSION[user]");
						$statement->execute();
						while ($member_id = $statement->fetch(PDO::FETCH_ASSOC))
						{
							$statementTWO = $db->prepare("SELECT first_name, last_name FROM project.user WHERE id=$member_id[member_id]");
							$statementTWO->execute();
							$member_name = $statementTWO->fetch(PDO::FETCH_ASSOC);
							
							$myAccSt->execute(array(':user_id' => $member_id['member_id']));
							while ($account = $myAccSt->fetch(PDO::FETCH_ASSOC))
							{
								echo '<tr onclick="postToAccountDetails(this)" style="cursor: pointer;" var="'. $account['account_id'] .'">';
								echo "<td>$member_name[first_name] $member_name[last_name]</td>";
								echo "<td>$account[name]</td>";
								echo "<td>$account[number]</td>";
								echo "<td>\$$account[amount]</td>";
								echo "</tr>";
							}
						}
					?>
				</tbody>
			</table>
		</div>
		
		<script>
			function postToAccountDetails(ele) {
				var id = $(ele).attr('var');
				$('.account_id').attr("value", id);
				$('.account-details').submit();
			}
		</script>
		
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
		<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
		
		<script src="transfer.js"></script>
		<link rel="stylesheet" type="text/css" href="transfer.css">
		

	</body>
</html>