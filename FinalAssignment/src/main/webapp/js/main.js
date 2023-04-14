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

			interval = setInterval(function() {
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
			}, 100);
		}

		function stopChart() {
			clearInterval(interval);
		}