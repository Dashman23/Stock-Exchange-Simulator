let chart;
let interval;
let ws = new WebSocket('ws://localhost:8080/FinalAssignment-1.0-SNAPSHOT/ws/stocks');

/*{
	"stocks": [
	{
		"symbol":"ASD",
		"held":54,
	},
	{
		"symbol":"ZXC",
		"held":22,
	},
	{
		"symbol":"QWE",
		"held":1,
	}
],
	"balance":5000.0
}*/
ws.onmessage = function (event) {
	console.log(event.data);
	let balance = JSON.parse(event.data.balance);
	let stock = JSON.parse(event.data.stocks);
	document.getElementById("Wallet").innerHTML = "Wallet: $ " + balance;
	for (let i = 0; i < stock.length; i++) {
		let id = "held" + i;								//use this to iterate over tds to update proper values in the portfolios ("price" + 1), ("price" + ... )
		document.getElementById(id).innerHTML = stock[i].held;
	}

	//come back to this
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

	// interval = setInterval(function() {
	//
	// 	let time = new Date().toLocaleTimeString();
	// 	chart.data.labels.push(time);
	// 	for (let i = 0; i < 4; i++) {
	// 		let stockPrice = Math.floor(Math.random() * 100) + 1;
	// 		chart.data.datasets[i].data.push(stockPrice);
	// 		if (chart.data.labels.length > 10) {
	// 			chart.data.labels.shift();
	// 			chart.data.datasets[0].data.shift();
	// 			chart.data.datasets[1].data.shift();
	// 			chart.data.datasets[2].data.shift();
	// 			chart.data.datasets[3].data.shift();
	// 		}
	// 	}
	// 	chart.update();
	// }, 5000);

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
					let id = "global" + i;								//use this to iterate over tds to update proper values in the portfolios ("price" + 1), ("price" + ... )
					document.getElementById(id).innerHTML = (+response.stocks[i].held); //updates portfolio prices for all stocks
				}
			})
		chart.update(); // updates chart
	}, 5000);// left it on 500 to see if it works through faster tick speed
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

		quantity.push({
			symbol: symbol,
			quantity: totalQuant
		});
	}

	const final = [];
	final.push({
		type: "request",
		quantities: quantity
	});

	console.log(JSON.stringify(final));
	ws.send(JSON.stringify(final));
}

(function (){

})();