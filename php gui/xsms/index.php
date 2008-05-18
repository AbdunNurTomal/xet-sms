<?php
// parametri del database
$db_host = "localhost";
$db_user = "root";
$db_password = "texrulez";
$db_name = "xsms";
$db_resultsPerPage = 5;
$db_currentConfigID = 1;

// il nome di questo file con estensione
$me = 'index.php';

// versione del presente script
$xsms_version = 'b1.0';

// testo iniziale
$headerText = '
	<html>
	<head>
	<title>Xsms System. Powered by Zip Projects</title>
	<link href="default.css" rel="stylesheet" type="text/css">
  <script language="JavaScript" src="blink.js"></script>
  <script language="JavaScript" src="textCountdown.js"></script>
	</head>
	<body onload="blinker(\'ff0000\', \'ffff00\', 30)">
  <a id="logozip" href="http://zip-progetti.it">Zip Projects</a>
  <a id="logoradio" href="http://radioreporter97.it">Radio Reporter</a>
	';
// testo finale
$footerText = '
  <div id="footer">
  Xsms System sviluppato da Emanuele Tessore per Zip Progetti
  versione '.$xsms_version.'
  </div>
  </body></html>
  ';

// calcola l'url che porta alla prossima pagina di risultati
function nextPage(){
  global $HTTP_SERVER_VARS;
  $url = $HTTP_SERVER_VARS["HTTP_HOST"] . $HTTP_SERVER_VARS["REQUEST_URI"];
  if(substr_count($url, '?')==0) return $me;
  if(substr_count($tok, 'page=')==0) $url.='&page=0';
  $url = substr($url, strpos($url, '?'));
  $tok = strtok($url, "&");
  $nextUrl = '';
  $status = false;
  while ($tok !== false) {
    if($status) $tok = strtok("&");
    if(substr_count($tok, 'page=')!=0){
      $currentpage = substr($tok, 5);
      $nextpage = intval($currentpage)+1;
      $tok = str_replace($currentpage, $nextpage, $tok);
      $status = true;
    }
    if($nextUrl !== '')$nextUrl .= '&'.$tok;
    else $nextUrl .= $tok;
    $tok = strtok("&");
  }
  return $nextUrl;
}
// calcola l'url che porta alla precedente pagina di risultati
function prevPage(){
  global $HTTP_SERVER_VARS, $me;
  $url = $HTTP_SERVER_VARS["HTTP_HOST"] . $HTTP_SERVER_VARS["REQUEST_URI"];
  if(substr_count($url, '?')==0) return $me;
  if(substr_count($tok, 'page=')==0) $url.='&page=1';
  $url = substr($url, strpos($url, '?'));
  $tok = strtok($url, "&");
  $nextUrl = '';
  $status = false;
  while ($tok !== false) {
    if($status) $tok = strtok("&");
    if(substr_count($tok, 'page=')!=0){
      $currentpage = substr($tok, 5);
      if($currentpage == 0)$currentpage = '1';
      $nextpage = intval($currentpage)-1;
      $tok = str_replace($currentpage, $nextpage, $tok);
      $status = true;
    }
    if($nextUrl !== '')$nextUrl .= '&'.$tok;
    else $nextUrl .= $tok;
    $tok = strtok("&");
  }
  return $nextUrl;
}

// elabora il menu di navigazione
function menu(){
  global $me;
	print ("
  <div id=\"navigation\">
  <ul>
    <li><a href=".prevPage()."><<</a></li>
    <li><a href=$me?action=diretta&page=0>Diretta</a></li>
    <li><a href=$me?action=printMessages&page=0>Messaggi</a></li>
	  <li><a href=$me?action=printUsers&page=0>Utenti</a></li>
	  <li><a href=$me?action=sendMenu&page=0>Invia</a></li>
	  <li><a href=$me?action=configMenu&page=0>Amministra</a></li>
    <li><a href=".nextPage().">>></a></li>
	</ul></div><br>
  ");
}

// stampa i messaggi formattati
function printMessages() {
  if(!isset($_GET['page'])) $page=0;
  else $page = $_GET['page'];
  global $db_host, $db_user, $db_password, $db_name, $db_resultsPerPage;
  $db = mysql_connect($db_host, $db_user, $db_password);
  if ($db == FALSE) die ("Errore nella connessione.");
  
  mysql_select_db($db_name, $db)
  or die ("Errore nella selezione del database.");
  
  $offset = $db_resultsPerPage * $page;
  //$end = $offset + $db_resultsPerPage;
  $query = 'select * from messages order by date desc, time desc limit '.$offset.','.$db_resultsPerPage;
  
  //echo $query;

  $result = mysql_query($query, $db)or die("Query fallita: " . mysql_error() );
  while ($riga = mysql_fetch_array($result)){
    //printf ("ID: %s | Mittente: %s | Data: %s | Ora: %s | Testo: %s <br>", $riga[0], $riga[1], $riga[2], $riga[3], $riga[4]);
    //echo "<br>"; 
    if($riga[5]==0)$riga[5]='Non Letto';
    else if($riga[5]==1)$riga[5]='Letto in Onda';
    else if($riga[5]==2)$riga[5]='Scartato';
    print('<div id=messaggi>
      <ul>
        <li id="mittente">Mittente: </li><li id=mittente2>'.$riga[1].'</li>
        <li id="dataora">Data: </li><li id=dataora2>'.$riga[2].' '.$riga[3].'</li>
        <li id="status">Status: </li><li id=status2>'.$riga[5].'</li>
        <br>
        <li id="testo">Testo: </li><li id=testo2>'.$riga[4].'</li>
      </ul>
    </div><hr>');
  }
  mysql_free_result($result);
  mysql_close();
}

// stampa la rubrica formattata
function printUsers() {
  if(!isset($_GET['page'])) $page=0;
  else $page = $_GET['page'];
  global $db_host, $db_user, $db_password, $db_name, $db_resultsPerPage;
  $db = mysql_connect($db_host, $db_user, $db_password);
  if ($db == FALSE) die ("Errore nella connessione.");
  
  mysql_select_db($db_name, $db)
  or die ("Errore nella selezione del database.");
  
  $offset = $db_resultsPerPage * $page;
  $end = $offset + $db_resultsPerPage;
  $query = 'select * from users limit '.$offset.','.$end;
  $result = mysql_query($query, $db)or die("Query fallita: " . mysql_error() );
  while ($riga = mysql_fetch_array($result)){
    //printf ("ID: %s | nome: %s | cognome: %s | nick: %s | tel: %s <br>", $riga[0], $riga[1], $riga[2], $riga[3], $riga[4]);
    //echo "<br>";
    if($riga[1]=='null')$riga[1]='';
    if($riga[2]=='null')$riga[2]='';
    if($riga[3]=='null')$riga[3]='';
    if($riga[5]=='null')$riga[5]='';
    if($riga[6]=='null')$riga[6]='';
    print('<div id=utenti>
      <ul>
        <li id="nome">Nome: </li><li id=nome2>'.$riga[1].' "'.$riga[3].'" '.$riga[2].'</li>
        <li id="numero">Numero: </li><li id=numero2>'.$riga[4].'</li>
        <br>
        <li id="info">Info: </li><li id=info2>'.$riga[5].'</li>
        <li id="groups">Group: </li><li id=groups2>'.$riga[6].'</li>
      </ul>
    </div><hr>');
  }
    
  mysql_free_result($result);
  mysql_close();
}

// stampa il menu di configurazione
function configMenu() {
  featureUnavailable();
  /*global $me;
  printf('
	<form method="POST" action='.$me.'?action=configure enctype=multipart/form-data>
		<p>DB Host:     <input type="text" name="db_host" size="20"></p>
		<p>DB User:     <input type="text" name="db_user" size="20"></p>
		<p>DB Pass:     <input type="text" name="db_password" size="20"></p>
		<p>DB Name:     <input type="text" name="db_name" size="20"></p>
		<p>DB Linee:    <input type="text" name="db_linee" size="20"></p>
		<p>CSS:         <input type="text" name="css" size="20"></p>
		<p>Sim pin:     <input type="text" name="simpin" size="20"></p>
		<p>COM Port:    <input type="text" name="comport" size="20"></p>
    <p><input type="submit" value="Invia" name="B1">&nbsp;
		<input type="reset" value="Reset" name="B2"></p>
	</form>');*/
}

// elabora il menu di configurazione
function configure(){featureUnavailable();}

// imposta lo status del messaggio %id su 1
function setRead($id){
  global $db_host, $db_user, $db_password, $db_name;
  $db = mysql_connect($db_host, $db_user, $db_password);
    if ($db == FALSE) die ("Errore nella connessione.");
  mysql_select_db($db_name, $db)
    or die ("Errore nella selezione del database.");
  mysql_query('update messages set `status`=\'1\' where (id=\''.$id.'\')  ', $db);
}
// mostra il\gli ultimo\i messaggi arrivati finchè non si preme "letto"
// oppure per un dato tempo
function diretta(){
  /*
  aggiunta per i pulsanti di avvenuta lettura
  */
  if(isset($_GET['readid'])) setRead($_GET['readid']);
  /*fin aggiunta*/
  global $db_host, $db_user, $db_password, $db_name, $db_resultsPerPage, $headerText, $footerText;
  
  $db = mysql_connect($db_host, $db_user, $db_password);
  if ($db == FALSE) die ("Errore nella connessione.");
  
  mysql_select_db($db_name, $db) or die ("Errore nella selezione del database.");
  
  $query = 'select * from messages where status=0 order by date desc, time desc';
  
  $result = mysql_query($query, $db)or die("Query fallita: " . mysql_error());
  $newMessages = mysql_affected_rows();
  if($newMessages!=0){
    setcookie('LastReadSMSID',$prevSMSRead+1);
    $warning = '<span class="blink">>> Sono presenti '.$newMessages.' nuovi messaggi <<</span>';
  }
  else $warning = 'Non sono presenti nuovi messaggi';
   
  print $headerText;
  menu();
  print($warning);
  
  while ($riga = mysql_fetch_array($result)){
      //printf ("ID: %s | Mittente: %s | Data: %s | Ora: %s | Testo: %s <br>", $riga[0], $riga[1], $riga[2], $riga[3], $riga[4]);
      //echo "<br>"; 
    print('<div id=messaggi>
      <ul>
        <li id="mittente">Mittente: </li><li id=mittente2>'.$riga[1].'</li>
        <li id="dataora">Data: </li><li id=dataora2>'.$riga[2].' '.$riga[3].'</li>
        <li id="elimina"><a href=index.php?action=diretta&page=0&readid='.$riga[0].'>Elimina</a></li>
        <br>
        <li id="testo">Testo: </li><li id=testo2>'.$riga[4].'</li>
      </ul>
    </div><hr>');
  }
  mysql_free_result($result);
  mysql_close();
  
  print $footerText;
}

function isSendAllowed(){
  global $db_host, $db_user, $db_password, $db_name, $db_currentConfigID;
  $db = mysql_connect($db_host, $db_user, $db_password);
    if ($db == FALSE) die ("Errore nella connessione.");
  mysql_select_db($db_name, $db)
    or die ("Errore nella selezione del database.");
  $result = mysql_query('Select sendMessagesAllowed From config Where id ='.$db_currentConfigID, $db);
  $riga = mysql_fetch_array($result);
  return $riga[0]=='1';
}
function sendMenu(){
  global $me;
  if(isSendAllowed())
    print ('  
    <form action='.$me.'?action=sendSMS&page=0 method="post" name="newMsg">
      <p>Destinatario: <input type="text" name="recipient" size="20"></p>
      <p>Messaggio: <textarea name="msgText" rows="4" cols="40" wrap="soft"
        onKeyUp="textKey(this.form)"></textarea>
      <br><input value="160" size="3" name="msgCL" disabled>
      <input TYPE="HIDDEN" name="lenSSig" value="0">
      <input TYPE="HIDDEN" name="lenLSig" value="0">
      <input TYPE="HIDDEN" name="lenSysSig" value="0">
      <input type="checkbox" name="isFlash"> Flash
      <p><input type="submit" value="Invia" name="B1">&nbsp;
      <input type="reset" value="Reset" name="B2"></p>
    </form>
    ');
  else
    print('l\'amministratore ha disabilitato questa funzionalita\'');
}

function sendSMS(){
  //echo $_REQUEST['isFlash'];
  if(!isset($_REQUEST['recipient']) or !isset($_REQUEST['msgText'])) return;
  if(isset($_REQUEST['isFlash']) and $_REQUEST['isFlash']=='on') $_REQUEST['isFlash']=1;
  else $_REQUEST['isFlash'] = 0; 
  
  $query = "INSERT INTO outgoingmessages (recipient, isFlash, text) VALUES ('"
    .$_REQUEST['recipient']."','".$_REQUEST['isFlash']."','".$_REQUEST['msgText']."')";
  
  //echo $query;
  global $db_host, $db_user, $db_password, $db_name, $db_currentConfigID;
  $db = mysql_connect($db_host, $db_user, $db_password);
    if ($db == FALSE) die ("Errore nella connessione.");
  mysql_select_db($db_name, $db)
    or die ("Errore nella selezione del database.");
  if(mysql_query($query, $db))
      print('Il Messaggio e\' stato inserito in coda. Sara\' inviato tra poco.');;
  }

function featureUnavailable(){
  global $xsms_version;
  echo 'non disponibile in questa versione.<br>versione: '.$xsms_version;
}

//------------------------------------- main switch

if(!isset($_GET['action'])) $action="";
else $action = $_GET['action'];

switch($action) {
	case "diretta":
    diretta();
	break;
  
	case "printUsers":
		print $headerText;
    menu();
    printUsers();
		print $footerText;
	break;
	
	case "printMessages":
		print $headerText;
    menu();
    printMessages();
		print $footerText;
	break;
	
	case "configMenu":
		print $headerText;
    menu();
    configMenu();
		print $footerText;
	break;
	
	case "configure":
		print $headerText;
    menu();
    configure();
		print $footerText;
	break;

	case "sendMenu":
		print $headerText;
    menu();
    sendMenu();
		print $footerText;
	break;

	case "sendSMS":
		print $headerText;
    menu();
    sendSMS();
		print $footerText;
	break;
  
	default:
		print $headerText;
    menu();
    printMessages();
		print $footerText;
	break;
}

?>
