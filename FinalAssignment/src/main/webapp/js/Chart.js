var chart;
var interval;

function startChart() {
  // create the websocket
  ws = new WebSocket("ws:localhost:8080/FinalAssignment-1.0-SNAPSHOT/ws/stocks");

  var ctx = document.getElementById('chart').getContext('2d');
  chart = new Chart(ctx, {
    type: 'line',
    data: {
      labels: [],
      datasets: [
        {
          label: "Daniel",
          data: [0],
          borderColor: "blue",
          fill: false,
        },
        {
          label: "David",
          data: [0],
          borderColor: "red",
          fill: false,
        },
        {
          label: "Anthony",
          data: [0],
          borderColor: "green",
          fill: false,
        },
        {
          label: "Heisn",
          data: [0],
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
  });}