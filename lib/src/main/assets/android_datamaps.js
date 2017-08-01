function loadMap(){
		document.addEventListener("DOMContentLoaded", function(event) {
			loadingListener.loadSuccess();
		});
		document.getElementById('container').style.width = mapData.getWidth() + 'px';
	    document.getElementById('container').style.height = mapData.getHeight() + 'px';		
		
		 var countriesArray = JSON.parse(mapData.getCountriesJson());

		 var colorMap = new Object();
         var fillsMap = { defaultFill: mapData.getDefaultFillColor() };
		 var markersArray = new Array();
		 for (var i = 0 ; i < countriesArray.length; i++){
			var countryData = countriesArray[i];
			if (countryData.marker){
				markersArray.push({centered : countryData.country});
			}
			if (countryData.color != null){
				colorMap[countryData.country] = { fillKey : countryData.country};
                fillsMap[countryData.country] = countryData.color
			}
		 }
		 
         var map = new Datamap({
			element: document.getElementById('container'),
			projection: mapData.getProjection(),
			fills: fillsMap,
            data: colorMap
		});						
		
		map.addPlugin('markers', handleMarkers)
			
		 map.markers(
				markersArray,
				{defaultIcon: mapData.getMarkerIcon()}
			);
	}

	function handleMarkers (layer, data, options ) {
		var height = mapData.getMarkerIconHeight();
		var width = mapData.getMarkerIconWidth();			
		
		var self = this,
			fillData = this.options.fills,
			svg = this.svg;

		if ( !data || (data && !data.slice) ) {
		  throw "Datamaps Error - markers must be an array";
		}

		var markers = layer.selectAll('image.datamaps-marker').data( data, JSON.stringify );

		markers
		  .enter()
			.append('image')
			.attr('class', 'datamaps-marker')
			.attr('xlink:href', function( datum ) {
			  return datum.iconUrl || options.defaultIcon;
			})
			.attr('height', height)
			.attr('width', width)
			.attr('x', function ( datum ) {
			  var latLng;
			  if ( datumHasCoords(datum) ) {
				latLng = self.latLngToXY(datum.latitude, datum.longitude);
			  }
			  else if ( datum.centered ) {
				if ( datum.centered === 'USA' ) {
				  latLng = self.projection([-98.58333, 39.83333])
				} else {
				  latLng = self.path.centroid(svg.select('path.' + datum.centered).data()[0]);
				}
			  }
			  if ( latLng ) return latLng[0] - width /2 ;
			})
			.attr('y', function ( datum ) {
			  var latLng;
			  if ( datumHasCoords(datum) ) {
				latLng = self.latLngToXY(datum.latitude, datum.longitude);
			  }
			  else if ( datum.centered ) {		  
				  if ( datum.centered === 'USA' ) {
					  latLng = self.projection([-98.58333, 39.83333])
					} else {
					  latLng = self.path.centroid(svg.select('path.' + datum.centered).data()[0]);
					}              
			  }
			  if ( latLng ) return latLng[1] - height;
			})

	   
		markers.exit()
		  .transition()
			.attr("height", 0)
			.remove();

		function datumHasCoords (datum) {
		  return typeof datum !== 'undefined' && typeof datum.latitude !== 'undefined' && typeof datum.longitude !== 'undefined';
		}

	}	