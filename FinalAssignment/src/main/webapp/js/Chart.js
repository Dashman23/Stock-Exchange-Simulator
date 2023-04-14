// Get the canvas element
var ctx = document.getElementById("myChart").getContext("2d");

// Create the chart data
var chartData = {
  labels: ["January", "February", "March", "April", "May", "June"],
  datasets: [
    {
      label: "Stock Price",
      data: [100, 120, 150, 200, 250, 300],
      borderColor: "blue",
      fill: false,
    },
    {
      label: "Expenses",
      data: [50, 80, 100, 120, 150, 200],
      borderColor: "red",
      fill: false,
    },
  ],
};

// Create the chart options
var chartOptions = {
  responsive: true,
  title: {
    display: true,
    text: "Group Project Stock Exchange",
  },
  scales: {
    yAxes: [
      {
        ticks: {
          beginAtZero: true,
        },
      },
    ],
  },
};

// Create the chart
var myChart = new Chart(ctx, {
  type: "line",
  data: chartData,
  options: chartOptions,
});
