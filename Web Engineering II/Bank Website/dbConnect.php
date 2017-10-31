<?php

	try {
		$dbUrl = getenv('DATABASE_URL');
		
		$dbopts = parse_url($dbUrl);

		$dbHost = $dbopts["host"];
		$dbPort = $dbopts["port"];
		$dbUser = $dbopts["user"];
		$dbPassword = $dbopts["pass"];
		$dbName = ltrim($dbopts["path"],'/');

		$db = new PDO("pgsql:host=$dbHost;port=$dbPort;dbname=$dbName", $dbUser, $dbPassword);		

		$db->setAttribute(PDO::ATTR_EMULATE_PREPARES, false);
		$db->setAttribute( PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION );
				
		$accSt = $db->prepare("SELECT number, name, amount FROM project.account AS a INNER JOIN project.account_type AS at ON a.id = :account_id AND a.type_id = at.id");
		$transSt = $db->prepare("SELECT name, info, time, amount FROM project.history AS h INNER JOIN project.history_entry AS he ON h.account_id = :account_id AND h.history_entry_id = he.id INNER JOIN project.history_type AS ht ON he.type_id = ht.id");
		$myAccSt = $db->prepare("SELECT number, name, amount, account_id FROM project.user_account AS ua INNER JOIN project.account AS a ON ua.account_id = a.id AND ua.user_id = :user_id INNER JOIN project.account_type AS at ON a.type_id = at.id");
		$fmAccSt = $db->prepare("SELECT number, name, amount, account_id FROM project.user_account AS ua INNER JOIN project.user_member AS um ON um.user_id = :user_id AND ua.user_id = um.member_id INNER JOIN project.account AS a ON ua.account_id = a.id INNER JOIN project.account_type AS at ON a.type_id = at.id");
	}
	catch (PDOException $ex) {
		echo "Error connecting to database: $ex";
		die();
	}
?>