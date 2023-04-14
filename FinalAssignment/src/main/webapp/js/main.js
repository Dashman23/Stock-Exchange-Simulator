var chart;
var interval;

function startChart() {
	// create the websocket
	ws = new WebSocket("ws:localhost:8080/FinalAssignment-1.0-SNAPSHOT/ws/stocks");

	var ctx = document.getElementById('chart').getContext('2d');
	chart = new Chart(ctx, {
		type: 'line',
		data: {
			datasets: [
				{
					label: "Daniel",
					data: [],
					borderColor: "blue",
					fill: false,
				},
				{
					label: "David",
					data: [],
					borderColor: "red",
					fill: false,
				},
				{
					label: "Anthony",
					data: [],
					borderColor: "green",
					fill: false,
				},
				{
					label: "Heisn",
					data: [],
					borderColor: "yellow",
					fill: false,
				},
			]
		},
		options: {
			responsive: true,
			animation: false,
			scales: {
				xAxes: [{
					display: true,
					ticks: {
						maxTicksLimit: 5
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

		callURL = "http://localhost:8080/FinalAssignment-1.0-SNAPSHOT/api/stock-data/json"
		fetch(callURL, {
			method: 'GET',
			headers: {
				'Accept': 'application/json',
			},
		})
			.then(response => response.text())
			.then(response => JSON.parse(response))
			.then(response => {
				var time = new Date().toLocaleTimeString();
				chart.data.labels.push(time);
				for(let i = 0; i < response.stocks.length; i++){
					var stockName = response.stocks[i].symbol;
					var stockPrice = (+response.stocks[i].price);
					chart.data.datasets[i].data.push(+stockPrice);
					if (chart.data.labels.length > 10) {
						chart.data.labels.shift();
						chart.data.datasets[i].data.shift();
					}
				}
			})
		chart.update();
	}, 500);
}