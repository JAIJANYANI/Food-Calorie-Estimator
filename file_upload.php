<?php
    $target_path = "./";
   // echo "uploading started";
    $target_path = $target_path . basename( $_FILES['uploadedfile']['name']);
 
    if(move_uploaded_file($_FILES['uploadedfile']['tmp_name'], $target_path))
        {
  //  echo "The file ".  basename( $_FILES['uploadedfile']['name'])." has been uploaded";
    chmod ("uploads/".basename( $_FILES['uploadedfile']['name']), 0644);
$data = "./image1.jpg";
//$data = "../../../../var/www/html/$_FILES['uploadedfile']['tmp_name']";
//$data = "../../../../../../home/jaijanyani/image1.jpg"
$data = "../../../../../../var/www/html/".basename( $_FILES['uploadedfile']['name']);
$data_string = $data;
$ch = curl_init('http://35.184.99.106:5000/');
curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "POST");
curl_setopt($ch, CURLOPT_POSTFIELDS, $data);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_HTTPHEADER, array('Content-Type: application/json','Content-Length: ' . strlen($data_string))
);		
$result = curl_exec($ch);
echo  $result ;
echo "\nThis is result" . $result;
//$result2 = ['result' => '$result' ];
//header('Content-type: application/json');
//echo json_encode( $data );
//echo "This is result inif : " . $data;
//echo "This is result inif : " . $result; 
   }

else{


echo "This is result inelse : " . $result;
echo "This is result inelse : " . $data;
//    echo "There was an error uploading the file, please try again!";
//echo $result;
  //  echo "filename: " .  basename( $_FILES['uploadedfile']['name']);
    //echo "target_path: " .$target_path;



}
