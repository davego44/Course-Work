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
		<title>David Banking Account</title>
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
		<div style="margin: 0 10%;">
			<h3>Account Details</h3>
			<table class="table">
				<thead>
					<tr>
						<th>Type</th>
						<th>Number</th>
						<th>Balance</th>
					</tr>
				</thead>
				<tbody>
					<?php
						$accSt->execute(array(':account_id' => $_POST['account_id']));
						$account = $accSt->fetch(PDO::FETCH_ASSOC);
						
						echo "<tr>";
						echo "<td>$account[name]</td>";
						echo "<td>$account[number]</td>";
						echo "<td>\$$account[amount]</td>";
						echo "</tr>";
					?>
				</tbody>
			</table>
			<br/>
			<h4>Account History</h4>
			<form action="" method="POST" class="form-inline">
				<div class="form-group">
					<input type="text" name="search" class="form-control" />
					<input type="hidden" name="account_id" value="<?php echo $_POST['account_id'];?>"/>
					<input type="submit" value="Search"  class="btn btn-primary" />
				</div>
			</form>
			<br/>
			<table class="table">
				<thead>
					<tr>
						<th>Type</th>
						<th>Info</th>
						<th>Time</th>
						<th>Amount</th>
					</tr>
				</thead>
				<tbody>
					<?php
						if(isset($_POST['search']) && !($_POST['search'] == "")) {
							$transSt->execute(array(':account_id' => $_POST['account_id']));
							while ($trans = $transSt->fetch(PDO::FETCH_ASSOC)) {
								if(strpos($trans['name'], $_POST['search'])    !== false ||
								   strpos($trans['info'], $_POST['search'])    !== false ||
								   strpos($trans['time'], $_POST['search'])    !== false ||
								   strpos($trans['amount'], $_POST['search'])  !== false) {
									   if ($trans['name'] == "Charge") {
										   echo "<tr class='danger'>";
									   } else if ($trans['name'] == "Credit") {
										   echo "<tr class='success'>";
									   } else {
										   echo "<tr>";
									   }
									   echo "<td>$trans[name]</td>";
									   echo "<td>$trans[info]</td>";
									   echo "<td>$trans[time]</td>";
									   echo "<td>$trans[amount]</td>";
									   echo "</tr>";
								}
							}
						} else {				
							$transSt->execute(array(':account_id' => $_POST['account_id']));
							while ($trans = $transSt->fetch(PDO::FETCH_ASSOC))
							{	
								if ($trans['name'] == "Charge") {
								   echo "<tr class='danger'>";
							   } else if ($trans['name'] == "Credit") {
								   echo "<tr class='success'>";
							   } else {
								   echo "<tr>";
							   }
							   echo "<td>$trans[name]</td>";
							   echo "<td>$trans[info]</td>";
							   echo "<td>$trans[time]</td>";
							   echo "<td>$trans[amount]</td>";
							   echo "</tr>";
							}
						}
					?>
				</tbody>
			</table>
		</div>

		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
		<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
		
		<link rel="stylesheet" type="text/css" href="transfer.css">
		<script src="transfer.js"></script>
	</body>
</html>