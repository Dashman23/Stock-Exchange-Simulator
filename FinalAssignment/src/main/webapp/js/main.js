let chart;
let interval;
let open = false;
let ws;

function server(){

// create the websocket
	ws = new WebSocket('ws://localhost:8080/FinalAssignment-1.0-SNAPSHOT/ws/stocks');

	ws.onopen = function () {
		let request = {"type":"balance request","message":"22.2"};
		//ws.send(JSON.stringify(request));
		ws.send(JSON.stringify(request));
		console.log("Currently Onopen");
	}

// parse messages received from the server and update the UI accordingly
	ws.onmessage = function (event) {
		console.log(event);
		let message = JSON.parse(event.data);
		document.getElementById("Wallet").innerHTML = "Wallet: $" + message.wallet;
	}

	ws.onerror = function (event) {
		console.error(event);
	}

	ws.onclose = function (event) {
		console.log("WebSocket is closed now.");
	}

}

function startChart() {

	let ctx = document.getElementById('chart').getContext('2d');
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


	interval = setInterval(function () {

			//retrieving stock prices and updating interface
			callURL = "http://localhost:8080/FinalAssignment-1.0-SNAPSHOT/api/stock-data/json"
			fetch(callURL, {
				method: 'GET',
				headers: {
					'Accept': 'application/json',
				},
			})
				.then(response => response.text())
				.then(response => JSON.parse(response))                        //parses response to json
				.then(response => {											//if in a pair of curly braces the response can be used in this isolated scope
					let time = new Date().toLocaleTimeString();
					chart.data.labels.push(time);							//time stamps needs to be uniform, so it is outside the loop
					for (let i = 0; i < response.stocks.length; i++) {
						let stockName = response.stocks[i].symbol;			//not needed for now have it just in case
						let stockPrice = (+response.stocks[i].price);		//price converts to number here
						let id = "price" + i;								//use this to iterate over tds to update proper values in the portfolios ("price" + 1), ("price" + ... )
						document.getElementById(id).innerHTML = stockPrice; //updates portfolio prices for all stocks
						chart.data.datasets[i].data.push(+stockPrice);		//adds the newest stock price to graph
						if (chart.data.labels.length > 10) {
							chart.data.labels.shift();						//once there is more than 10 points it deletes the last node with shift
							chart.data.datasets[i].data.shift();			//this once deletes the x label associated
						}
					}
				})
			chart.update(); // updates chart
		}, 5000);// left it on 500 to see if it works through faster tick speed
}

(function (){

})();