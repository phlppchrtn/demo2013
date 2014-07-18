<html>
<head>
	<meta charset="utf-8">
	<title>locations</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	
	<!-- Le styles -->
	<link href="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.2.2/css/bootstrap-combined.min.css" rel="stylesheet">
	<style>	
		body {
			padding-top: 60px; /* 60px to make the container go all the way to the bottom of the topbar */
			background-color: #fff;
		}
		#map img { 
			  max-width: none;
		}
	</style>	
	<script src="http://maps.google.com/maps/api/js?sensor=false" type="text/javascript" ></script>
	<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.9.0/jquery.min.js"></script>

	<!-- Fav and touch icons -->
	<link rel="shortcut icon" href="assets/ico/favicon.png">
</head>

<body>
	<h1>Liste des lieux (gmaps)</h1>
	<div id="map" style="width: '100%'; height: 500px;"></div>
		
	<input type='hidden' id='locations' value='${locations}' />

	<script type="text/javascript">
		var initialize  = function (){
	    	var gmap = new google.maps.Map(document.getElementById('map'), {
      			zoom: 10,
      			center: new google.maps.LatLng(-33.92, 151.25),
      			mapTypeId: google.maps.MapTypeId.ROADMAP
    		});

			var locations = jQuery('#locations').val();
			locations =  jQuery.parseJSON(locations); 

    		var latitudeSum =0;
    		var longitudeSum =0;
    		var hits=0;
    		var marker;
    		for (var i in locations) {
    			var location = locations[i];
        	
      			marker = new google.maps.Marker({
        			position: new google.maps.LatLng(location.lat, location.lng),
        			map: gmap
      			});

        		latitudeSum += location.lat;
        		longitudeSum += location.lng;
        		hits ++;
			}

		    gmap.center = new google.maps.LatLng( latitudeSum/hits, longitudeSum/hits);
		};    
		google.maps.event.addDomListener(window, 'load', initialize);
  		</script>
	</body>
</html>