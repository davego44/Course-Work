<?php
	require("dbConnect.php");
	
	session_start();
	
	if(isset($_SESSION['user'])){
		header("location: home.php");
    }
	
	$identity = $first = $middle = $last = $username = $password = "";
	$email = $driversLic = $address = $phone = $SSN = $birthdate = "";
	
	$login_error = $identityError = $firstError = $lastError = $usernameError = $passwordError = "";
	$emailError = $driversLicError = $addressError = $phoneError = $SSNError = $birthdateError = "";
	$conPasswordError = $createAccountError = "";
	
	if($_SERVER["REQUEST_METHOD"] == "POST") {
		if ($_POST['type'] == "login") {
			$statement = $db->prepare("SELECT id FROM project.user WHERE username=:username AND password=:password");
			$statement->execute(array(':username' => $_POST['username'], ':password' => $_POST['password']));
			$results = $statement->fetch(PDO::FETCH_ASSOC);
			if(isset($results['id'])) {
				$_SESSION['user'] = $results['id'];
				header("location: home.php");
			} else {
				$login_error = "Login username or password was not found.";
			}
		} else if ($_POST['type'] == "create") {
			if (empty($_POST['identity'])) {
				$identityError = "You must enter your identity number.";
			} else {
				$identity = test_input($_POST['identity']);
			}
			if (empty($_POST['firstName'])) {
				$firstError = "You must enter your first name.";
			} else {
				$first = test_input($_POST['firstName']);
			}
			if (empty($_POST['lastName'])) {
				$lastError = "You must enter your last name.";
			} else {
				$last = test_input($_POST['lastName']);
			}
			if (empty($_POST['username'])) {
				$usernameError = "You must enter a username.";
			} else {
				$username = test_input($_POST['username']);
			}
			if (empty($_POST['password'])) {
				$passwordError = "You must enter a password.";
			} else {
				$password = test_input($_POST['password']);
			}
			if (empty($_POST['conPassword'])) {
				$conPasswordError = "You must confirm your password.";
			} else {
				if ($_POST['conPassword'] !== $_POST['password']) {
					$passwordError = "Passwords must match.";
					$conPasswordError = "Passwords must match.";
				}
			}
			if (empty($_POST['email'])) {
				$emailError = "You must enter your email.";
			} else {
				$email = test_input($_POST['email']);
			}
			if (empty($_POST['driversLic'])) {
				$driversLicError = "You must enter your drivers license.";
			} else {
				$driversLic = test_input($_POST['driversLic']);
			}
			if (empty($_POST['address'])) {
				$addressError = "You must enter your address.";
			} else {
				$address = test_input($_POST['address']);
			}
			if (empty($_POST['phone'])) {
				$phoneError = "You must enter your phone number.";
			} else {
				$phone = test_input($_POST['phone']);
			}
			if (empty($_POST['ssn'])) {
				$SSNError = "You must enter your social security number.";
			} else {
				$SSN = test_input($_POST['ssn']);
			}
			if (empty($_POST['birthdate'])) {
				$birthdateError = "You must enter your birthdate.";
			} else {
				$birthdate = test_input($_POST['birthdate']);
			}
			if (!empty($_POST['identity'])    && !empty($_POST['firstName']) && !empty($_POST['lastName'])  &&
			    !empty($_POST['username'])    && !empty($_POST['password'])  && !empty($_POST['email'])     &&
				!empty($_POST['driversLic'])  && !empty($_POST['address'])   && !empty($_POST['phone'])     &&
				!empty($_POST['phone'])       && !empty($_POST['ssn'])       && !empty($_POST['birthdate']) &&
				!empty($_POST['conPassword']) && $_POST['conPassword'] == $_POST['password']) {
						$middle = test_input($middle);
						try {
							$st = $db->prepare("INSERT INTO project.user (username, password, first_name, middle_name, last_name, email, phone, address, ssn, birthdate, drivers_lic)
											VALUES(:username, :password, :first_name, :middle_name, :last_name, :email, :phone, :address, :ssn, :birthdate, :drivers_lic)");
							$st->execute(array(':username' => $username, ':password' => $password, 
											   ':first_name' => $first, ':middle_name' => $middle, 
											   ':last_name' => $last, ':email' => $email,
											   ':phone' => str_replace("-", "", $phone), ':address' => $address, 
											   ':ssn' => str_replace("-", "", $SSN), ':birthdate' => $birthdate,
											   ':drivers_lic' => $driversLic));
							$_SESSION['user'] = (int)$db->lastInsertId();
							header("location: home.php");
						} catch (PDOException $e) {
							$createAccountError = "Unable to create account.";
							if ($e->getCode() == 23505) {
								$usernameError = "Username is already taken.";
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
		<title>David Banking Login</title>
		<link rel="stylesheet" href="login.css">
		<script>
			function checkPasswordMatch() {
				var password = $("#password").val();
				var conPassword = $("#conPassword").val();
				
				if (password != conPassword)
					$("#conPasswordError").html("Passwords must match.");
				else
					$("#conPasswordError").html("");
			}
		</script>
	</head>
	<body>
		<div class="top-bar">
			<h1 class="title">David Banking</h1>
		</div>
		<nav class="navbar navbar-default">
			<div class="center-page-h">
				<ul class="nav navbar-nav">
					<li class="active"><a href="#">Home</a></li>
					<li class="dropdown">
						<a class="dropdown-toggle" data-toggle="dropdown" href="#">
							About<span class="caret"></span></a>
						<ul class="dropdown-menu">
							<li><a href="#">FAQ</a></li>
						</ul>
					</li>
					<li><a href="#">Contact</a></li>
				</ul>
				<ul class="nav navbar-nav navbar-right">
					<li><a href="#signup"><span class="glyphicon glyphicon-user"></span> Sign Up</a></li>
				</ul>
			</div>
		</nav>
		<div class="center-page-h center-container">
			<div class="image-container" style="position: relative;">
				<div class="my-image-text-container" style="position: absolute; color: lightgray; left: 0; bottom: 0; margin-left: 2%; margin-bottom: 1%; font-style: italic;">
					<h2>Bringing People Together</h2>
				</div>
				<div class="login-container" style="position: absolute;">
					<div class="login-title-container">
						<div class="login-title-content-container">
							<span class="glyphicon glyphicon-log-in" style="margin-right: 8px; margin-left: 5px;"></span>Login
						</div>
					</div>
					<div class="login-content-container">
						<form action="" method="POST">
							<br/>
							<div class="form-group">
								<label for="username" style="color: lightgray;">Username</label>
								<input name="username" type="text" class="form-control"/>
							</div>
							<div class="form-group">
								<label for="password" style="color: lightgray;">Password</label>
								<input name="password" type="password" class="form-control"/>
							</div>
							<?php 
								if ($login_error !== "") {
									echo '<div class="error">' . $login_error . '</div>';
								} else {
									echo "<br/>";
								}
							?>
							<input type="hidden" value="login" name="type"/>
							<input type="submit" value="Login" class="btn btn-primary"/>
						</form>
						
					</div>
				</div>
				<img src="Project1BankImg.jpg" alt="Group of people">
			</div>
			<h3>Welcome to David Banking</h3>
			We are proud to be among the top banking firms in the United States and we are excited to share our experience with you.<br/>
			Our mission is to provide secure, fast, and accessible banking for all.<br/>
			<div class="row">
				<div class="col-md-6">
					<h3>Get Started</h3>
					Don't have an account? Go ahead and fill out the required info below!<br/>
					<br/>
					<form action="#signup" method="POST" id="signup">
						<div class="form-group">
							<label for="identity">Identity Number (given by banker)</label><span style="color:red;"> *</span>
							<input name="identity" type="text" class="form-control" aria-describedby="identityError" value="<?php echo $identity;?>"/>
							<small id="identityError" class="form-text text-muted" style="color:red;"><?php echo $identityError;?></small>
						</div>
						<div class="row">
							<div class="col-md-4">
								<div class="form-group">
									<label for="firstName">First Name</label><span style="color:red;"> *</span>
									<input name="firstName" type="text" class="form-control" aria-describedby="firstError" maxlength="30" value="<?php echo $first;?>"/>
									<small id="firstError" class="form-text text-muted" style="color:red;"><?php echo $firstError;?></small>
								</div>
							</div>
							<div class="col-md-4">
								<div class="form-group">
									<label for="middleName">Middle Name</label>
									<input name="middleName" type="text" class="form-control" maxlength="30" value="<?php echo $middle;?>"/>
								</div>
							</div>
							<div class="col-md-4">
								<div class="form-group">
									<label for="lastName">Last Name</label><span style="color:red;"> *</span>
									<input name="lastName" type="text" class="form-control" aria-describedby="lastError" maxlength="30" value="<?php echo $last;?>"/>
									<small id="lastError" class="form-text text-muted" style="color:red;"><?php echo $lastError;?></small>
								</div>
							</div>
						</div>
						<div class="form-group">
							<label for="username">Username</label><span style="color:red;"> *</span>
							<input name="username" type="text" class="form-control" aria-describedby="usernameError" maxlength="30" value="<?php echo $username;?>"/>
							<small id="usernameError" class="form-text text-muted" style="color:red;"><?php echo $usernameError;?></small>
						</div>
						<div class="row">
							<div class="col-md-6">
								<div class="form-group">
									<label for="password">Password</label><span style="color:red;"> *</span>
									<input name="password" id="password" type="password" class="form-control" aria-describedby="passwordError" maxlength="30" value="<?php echo $password;?>" onkeyup="checkPasswordMatch();"/>
									<small id="passwordError" class="form-text text-muted" style="color:red;"><?php echo $passwordError;?></small>
								</div>
							</div>
							<div class="col-md-6">
								<div class="form-group">
									<label for="conPassword">Confirm Password</label><span style="color:red;"> *</span>
									<input name="conPassword" id="conPassword" type="password" class="form-control" aria-describedby="conPasswordError" maxlength="30" onkeyup="checkPasswordMatch();"/>
									<small id="conPasswordError" class="form-text text-muted" style="color:red;"><?php echo $conPasswordError;?></small>
								</div>
							</div>
						</div>
						<div class="row">
							<div class="col-md-6">
								<div class="form-group">
									<label for="email">Email</label><span style="color:red;"> *</span>
									<input name="email" type="email" class="form-control" placeholder="name@example.com" aria-describedby="emailError" maxlength="35" value="<?php echo $email;?>"/>
									<small id="emailError" class="form-text text-muted" style="color:red;"><?php echo $emailError;?></small>
								</div>
							</div>
							<div class="col-md-6">
								<div class="form-group">
									<label for="driversLic">Drivers License</label><span style="color:red;"> *</span>
									<input name="driversLic" type="text" class="form-control" aria-describedby="driversLicError" maxlength="30" value="<?php echo $driversLic;?>"/>
									<small id="driversLicError" class="form-text text-muted" style="color:red;"><?php echo $driversLicError;?></small>
								</div>
							</div>
						</div>
						<div class="form-group">
							<label for="address">Address</label><span style="color:red;"> *</span>
							<input name="address" type="text" class="form-control" aria-describedby="addressError" maxlength="200" value="<?php echo $address;?>"/>
							<small id="addressError" class="form-text text-muted" style="color:red;"><?php echo $addressError;?></small>
						</div>
						<div class="row">
							<div class="col-md-4">
								<div class="form-group">
									<label for="phone">Phone</label><span style="color:red;"> *</span>
									<input name="phone" type="tel" class="form-control" placeholder="123-456-7890" aria-describedby="phoneError" maxlength="12" value="<?php echo $phone;?>"/>
									<small id="phoneError" class="form-text text-muted" style="color:red;"><?php echo $phoneError;?></small>
								</div>
							</div>
							<div class="col-md-4">
								<div class="form-group">
									<label for="ssn">SSN</label><span style="color:red;"> *</span>
									<input name="ssn" type="text" class="form-control" pattern="[0-9]{3}\-?[0-9]{2}\-?[0-9]{4}" placeholder="123-45-6789" title="Must be in the format 123-45-6789 (no dashes is also accepted)" aria-describedby="SSNError" maxlength="11" value="<?php echo $SSN;?>"/>
									<small id="SSNError" class="form-text text-muted" style="color:red;"><?php echo $SSNError;?></small>
								</div>
							</div>
							<div class="col-md-4">
								<div class="form-group">
									<label for="birthdate">Birthdate</label><span style="color:red;"> *</span>
									<input name="birthdate" type="text" class="form-control" placeholder="yyyy-mm-dd" pattern="[1-2][0-9]{3}\-[0-9]{2}\-[0-9]{2}" title="Must be in the format yyyy-mm-dd (dashes must be included)" aria-describedby="birthdateError" maxlength="10" value="<?php echo $birthdate;?>"/>
									<small id="birthdateError" class="form-text text-muted" style="color:red;"><?php echo $birthdateError;?></small>
								</div>
							</div>
						</div>
						<input type="hidden" value="create" name="type"/>
						<input type="submit" value="Create Account" class="btn btn-primary" aria-describedby="createAccountError"/><br/>
						<small id="createAccountError" class="form-text text-muted" style="color:red;"><?php echo $createAccountError;?></small>
					</form>
				</div>
				<div class="col-md-6">
					<h3>Why Us?</h3>
					You are asking the right questions!<br/>
					<p>We stay up-to-date with the latest technology and do all we can to offer
					a safe and reliable interaction with our bank for all customers. We also have record-breaking top-notch speeds to allow our customers to spend their
					time their way and not with trivial matters. We have the best mobile app to allow you to bank on-the-go. We also employ only friendly employees to
					provide excellent and timely customer service.</p>
				</div>
			</div>
			
			<br/>

		</div>
		
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
		<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
	</body>
</html>