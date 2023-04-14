var chart;
var interval;

function startChart() {
	var ctx = document.getElementById('chart').getContext('2d');
	chart = new Chart(ctx, {
		type: 'line',
		data: {
			labels: [],
			datasets: [{
				label: 'Stock Price',
				data: [],
				borderColor: 'rgb(255, 99, 132)',
				fill: false
			}]
		},
		options: {
			responsive: true,
			animation: false,
			scales: {
				xAxes: [{
					display: true,
					ticks: {
						maxTicksLimit: 10
					}
				}],
				yAxes: [{
					display: true,
					ticks: {
						beginAtZero: true
					}
				}]
			}
		}
	});
	var socket = new WebSocket("ws://localhost:8080/FinalAssignment-1.0-SNAPSHOT/ws/stocks");
    socket.onmessage = function(event) {
        var stockData = JSON.parse(event.data);
        var stockPrice = stockData.price;
        var time = new Date(stockData.time).toLocaleTimeString();
        chart.data.labels.push(time);
        chart.data.datasets[0].data.push(stockPrice);
        if (chart.data.labels.length > 10) {
            chart.data.labels.shift();
            chart.data.datasets[0].data.shift();
        }
        chart.update();
    };

    interval = setInterval(function() {
        socket.send("request");
    }, 5000);
	/* A json like this will be returned
	{
		"symbol": "AAPL",
		"price": 145.63,
		"time": "2023-04-14T01:23:45.678Z"
	}
	*/
	/*interval = setInterval(function() {
		// Replace the below code with your actual stock data API
		if (ws != null) {
			ws.close();
		}

		// create the websocket
		ws = new WebSocket("http://localhost:8080/FinalAssignment-1.0-SNAPSHOT/ws/stocks");

		ws.onmessage = function (event) {
			console.log(event.data);
			// parsing the server's message as json
			let message = JSON.parse(event.data);
			document.getElementById("log").value += "(" + timestamp() + ") " + message.message + "\n";
		}

		var stockPrice = Math.floor(Math.random() * 100) + 1;
		var time = new Date().toLocaleTimeString();
		chart.data.labels.push(time);
		chart.data.datasets[0].data.push(stockPrice);
		if (chart.data.labels.length > 10) {
			chart.data.labels.shift();
			chart.data.datasets[0].data.shift();
		}
		chart.update();
	}, 100);*/
}

function stopChart() {
	clearInterval(interval);
}

/* {
        "symbol": "AAPL",
        "quantity": 145.63,
    }
	*/
function lockIn() {
	// Get the table and rows
	const table = document.getElementById("Order");
	const rows = table.getElementsByTagName("tr");
	
	// Create an array to store the data
	const quantities = [];
	
	// Loop through the rows and extract the data
	for (let i = 1; i < rows.length - 1; i++) { // skip header row and footer row
		const row = rows[i];
		const symbol = row.getElementsByTagName("td")[0].textContent;
		const buyPrice = parseFloat(row.getElementsByTagName("td")[1].querySelector("input").value);
		const sellPrice = parseFloat(row.getElementsByTagName("td")[2].querySelector("input").value);
		quantities.push({
		symbol: symbol,
		quantity: buyPrice
		}, {
		symbol: symbol,
		quantity: sellPrice
		});
	}
	
	// Create a JSON object with the form data
	const data = {
		quantities: quantities
	};
	
	// Send the data to the server via a POST request
	fetch("http://localhost:8080/FinalAssignment-1.0-SNAPSHOT/api/buy", { // change this later
		method: "POST",
		headers: {
			"Content-Type": "application/json"
		},
		body: JSON.stringify(data)
		})
		.then(response => {
		if (response.ok) {
			alert("Stock purchase successful!");
			document.getElementById("Order").reset();
		} else {
			alert("Error: " + response.statusText);
		}
		})
		.catch(error => {
		alert("Error: " + error.message);
		});
	}
	  
