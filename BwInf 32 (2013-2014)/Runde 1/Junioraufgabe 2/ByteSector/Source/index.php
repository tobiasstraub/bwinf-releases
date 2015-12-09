<html>
	<head>
		<title>BWINF 32 2013 Tim Schmidt J2</title>
	</head>
	<body>
	<a href="zollstock.php">Da Solution</a>
	<br />
<?php 
		function countUpBinary($binaryNumber, $summand){
			for($i = 0; $i < $summand; $i++){
				$binaryNumber = base_convert((base_convert($binaryNumber, 2, 10) + 1), 10, 2);
			}
			return $binaryNumber;
		}

		function isAtTheEnd($mainStr, $subStr){
			if(strlen($mainStr) >= strLen($subStr)){
				return (strrpos($mainStr, $subStr, strlen($mainStr) - strlen($subStr)) !== false);
			} else {
				return false;
			}
		}

		function rfr($chars){
			$valids = array();
			$seen = array();
			$removed = 0;
			$removedForReal = 0;
			$temp = "1";
			for($i = 0; $i < $chars; $i++){
				$temp .= "0";
			}
			$temp = (int)($temp);
	
			for($i = 0; $i < pow(2,$chars); $i++){
				array_push($valids, $temp);
				$temp = countUpBinary($temp, 1);
			}
	
			for($i = 0; $i < pow(2,$chars); $i++){
				#echo "Subject: $valids[$i]<br />";
				$clean = true;
				for($j = 0; $j < strlen($valids[$i]) - 1; $j++){
					if ((int)(substr($valids[$i], $j, 1)) + (int)(substr($valids[$i], $j + 1, 1)) == 0){
						$clean = false;
						#echo "Double 0!<br />";
						$alreadySeen = false;
						foreach ($seen as $val){
							#echo "Seen: $val<br />";
							# Wortvorkommen wird erkannt, aber nicht $seen hinzugefügt
							if (isAtTheEnd($valids[$i], $val)){
								#echo "Already seen...<br />";
								$alreadySeen = true;
								break;
							}
						}
						if (!$alreadySeen){
							#echo "NEW ONE <br />";
							array_push($seen, $valids[$i]);
							if (strlen($valids[$i]) == $chars){
								$removedForReal++;
							}
						}
						unset($valids[$i]);
						$removed++;
						break;
					}
				}
				#if ($clean){
				#	echo "Clean<br />";
				#}
			}
	
			$valids = array_values($valids);
			return $removedForReal;
		}

		for($i = 1; $i < 11; $i++){
			echo "Chars: $i <br />
					Removed (FR): " . rfr($i) . "<br /><br />";
		}

		/*echo 	"Valids: " . sizeof($valids) . "<br />
				Chars: $chars <br />
				Removed: $removed <br />
				Removed (FR): $removedForReal <br />";*/
		
		?>
	</body>
</html>