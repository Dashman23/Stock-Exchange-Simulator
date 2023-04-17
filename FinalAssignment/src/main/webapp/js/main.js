let chart;
let interval;
const ws = new WebSocket('ws://localhost:8080/FinalAssignment-1.0-SNAPSHOT/ws/stocks');
let ind;

ws.onmessage = function (event) {

	let jsonfile = JSON.parse(event.data);
	let balance = jsonfile.balance;
	let stock = jsonfile.stocks;

	document.getElementById("Wallet").innerHTML = "Wallet: $ " + parseFloat(balance).toFixed(2);
	for (let i = 0; i < stock.length; i++) {
		let id = "held"+stock[i].symbol;				//use this to iterate over tds to update proper values in the portfolios ("held" + name)
		document.getElementById(id).innerHTML = stock[i].held;
	}
}

function startChart() {

	let ctx = document.getElementById('chart').getContext('2d');
	chart = new Chart(ctx, {
		type: 'line',
		data: {
			datasets: [
				{
					label: "TSLA",
					data: [],
					borderColor: "blue",
					fill: false,
				},
				{
					label: "NTDO",
					data: [],
					borderColor: "red",
					fill: false,
				},
				{
					label: "UBER",
					data: [],
					borderColor: "green",
					fill: false,
				},
				{
					label: "SONY",
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
						maxTicksLimit: 20
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
		ws.send(JSON.stringify({"type": "update"}));

		//retrieving stock prices and updating interface
		callURL = "http://localhost:8080/FinalAssignment-1.0-SNAPSHOT/api/stock-data/stocksJson";
		fetch(callURL, {
			method: 'GET',
			headers: {
				'Accept': 'application/json',
			},
		})
			.then(response => response.text())
			.then(response => JSON.parse(response))                     //parses response to json
			.then(response => {											//if in a pair of curly braces the response can be used in this isolated scope
				let time = new Date().toLocaleTimeString();
				chart.data.labels.push(time);							//time stamps needs to be uniform, so it is outside the loop
				for (let i = 0; i < response.stocks.length; i++) {
					let stockName = response.stocks[i].symbol;			//not needed for now have it just in case
					let stockPrice = parseFloat(response.stocks[i].price).toFixed(2);		//price converts to number here
					let id = "price"+stockName;								//use this to iterate over tds to update proper values in the portfolios ("price" + 1), ("price" + ... )
					document.getElementById(id).innerHTML = stockPrice; //updates portfolio prices for all stocks
					for (let j = 0; j < response.stocks.length; j++) {
						if(stockName == chart.data.datasets[j].label){
							chart.data.datasets[j].data.push(stockPrice);
							ind = j;
						}
					}
				}
				if (chart.data.labels.length > 20) {
					chart.data.labels.shift();
					for (let i = 0; i < response.stocks.length; i++) {
						chart.data.datasets[i].data.shift();
					}//once there is more than 10 points it deletes the last node with shift
					//this once deletes the x label associated
				}
			})
		// retrieving stock prices and updating interface
		callURL = "http://localhost:8080/FinalAssignment-1.0-SNAPSHOT/api/stock-data/globalJson";
		fetch(callURL, {
			method: 'GET',
			headers: {
				'Accept': 'application/json',
			},
		})
			.then(response => response.text())
			.then(response => JSON.parse(response))                        //parses response to json
			.then(response => {											//if in a pair of curly braces the response can be used in this isolated scope
				//time stamps needs to be uniform, so it is outside the loop
				for (let i = 0; i < response.stocks.length; i++) {
					let id = "global" + response.stocks[i].symbol;								//use this to iterate over tds to update proper values in the portfolios ("price" + 1), ("price" + ... )
					document.getElementById(id).innerHTML = response.stocks[i].held; //updates portfolio prices for all stocks
				}
			})
		chart.update(); // updates chart
	}, 4000);// left it on 500 to see if it works through faster tick speed
}

function lockIn() {
	// Get the table and rows
	const table = document.getElementById("Order");
	const rows = table.getElementsByTagName("tr");
	let totalQuant = 0;
	// Create an array to store the data
	const quantity = [];

	// Loop through the rows and extract the data
	for (let i = 1; i < rows.length - 1; i++) { // skip header row and footer row
		const row = rows[i];
		const symbol = row.getElementsByTagName("td")[0].textContent;
		if(isNaN(parseFloat(row.getElementsByTagName("td")[1].querySelector("input").value)) ){
			totalQuant = 0;
		}
		else{
			totalQuant += parseFloat(row.getElementsByTagName("td")[1].querySelector("input").value);
		}
		if(isNaN(parseFloat(row.getElementsByTagName("td")[2].querySelector("input").value))){
			totalQuant += 0;
		}
		else{
			totalQuant -= parseFloat(row.getElementsByTagName("td")[2].querySelector("input").value);
		}

		id = "buy" + i;
		document.getElementById(id).value = "";

		id = "sell" + i;
		document.getElementById(id).value = "";

		quantity.push({
			symbol: symbol,
			quantity: totalQuant
		});
		totalQuant = 0;
	}

	let final = {
		type: "request",
		quantities: quantity
	};

	ws.send(JSON.stringify(final));
}

(function (){

})();