let currentInput = '';
let operator = '';
let previousInput = '';

function showTab(tabName) {
    // Hide all tabs
    document.querySelectorAll('.tab-content').forEach(tab => {
        tab.classList.remove('active');
    });
    
    // Remove active class from all buttons
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    
    // Show selected tab
    document.getElementById(tabName).classList.add('active');
    
    // Add active class to clicked button
    event.target.classList.add('active');
    
    // Clear display when switching tabs
    clearDisplay();
}

function appendToDisplay(value) {
    const display = document.getElementById('result');
    display.value += value;
}

function clearDisplay() {
    document.getElementById('result').value = '';
    currentInput = '';
    operator = '';
    previousInput = '';
}

function deleteLast() {
    const display = document.getElementById('result');
    display.value = display.value.slice(0, -1);
}

function calculate() {
    const display = document.getElementById('result');
    try {
        // Replace × with * for calculation
        let expression = display.value.replace(/×/g, '*');
        let result = eval(expression);
        
        // Handle division by zero
        if (!isFinite(result)) {
            display.value = 'Error: Division by zero';
            return;
        }
        
        display.value = result;
    } catch (error) {
        display.value = 'Error: Invalid expression';
    }
}

function scientificCalc(operation) {
    const input = document.getElementById('sci-input');
    const value = parseFloat(input.value);
    
    if (isNaN(value) && operation !== 'pi') {
        alert('Please enter a valid number');
        return;
    }
    
    let result;
    switch (operation) {
        case 'sqrt':
            if (value < 0) {
                alert('Cannot calculate square root of negative number');
                return;
            }
            result = Math.sqrt(value);
            break;
        case 'pow':
            result = Math.pow(value, 2);
            break;
        case 'sin':
            result = Math.sin(value * Math.PI / 180); // Convert to radians
            break;
        case 'cos':
            result = Math.cos(value * Math.PI / 180); // Convert to radians
            break;
        case 'log':
            if (value <= 0) {
                alert('Logarithm undefined for non-positive numbers');
                return;
            }
            result = Math.log10(value);
            break;
        case 'pi':
            result = Math.PI;
            break;
    }
    
    input.value = result;
}

function calculateScientific() {
    // This function can be used for more complex scientific calculations
    const input = document.getElementById('sci-input');
    alert(`Result: ${input.value}`);
}

function convertTemperature() {
    const input = parseFloat(document.getElementById('temp-input').value);
    const from = document.getElementById('temp-from').value;
    const to = document.getElementById('temp-to').value;
    const resultDiv = document.getElementById('temp-result');
    
    if (isNaN(input)) {
        resultDiv.textContent = 'Please enter a valid number';
        return;
    }
    
    let result;
    
    // Convert to Celsius first
    let celsius;
    switch (from) {
        case 'celsius':
            celsius = input;
            break;
        case 'fahrenheit':
            celsius = (input - 32) * 5/9;
            break;
        case 'kelvin':
            celsius = input - 273.15;
            break;
    }
    
    // Convert from Celsius to target
    switch (to) {
        case 'celsius':
            result = celsius;
            break;
        case 'fahrenheit':
            result = (celsius * 9/5) + 32;
            break;
        case 'kelvin':
            result = celsius + 273.15;
            break;
    }
    
    resultDiv.textContent = `${input}° ${from} = ${result.toFixed(2)}° ${to}`;
}

function convertLength() {
    const input = parseFloat(document.getElementById('length-input').value);
    const from = document.getElementById('length-from').value;
    const to = document.getElementById('length-to').value;
    const resultDiv = document.getElementById('length-result');
    
    if (isNaN(input)) {
        resultDiv.textContent = 'Please enter a valid number';
        return;
    }
    
    let result;
    
    // Convert to meters first
    let meters;
    switch (from) {
        case 'meters':
            meters = input;
            break;
        case 'feet':
            meters = input / 3.28084;
            break;
        case 'kilometers':
            meters = input * 1000;
            break;
    }
    
    // Convert from meters to target
    switch (to) {
        case 'meters':
            result = meters;
            break;
        case 'feet':
            result = meters * 3.28084;
            break;
        case 'miles':
            result = meters * 0.000621371;
            break;
    }
    
    resultDiv.textContent = `${input} ${from} = ${result.toFixed(4)} ${to}`;
}

function convertWeight() {
    const input = parseFloat(document.getElementById('weight-input').value);
    const from = document.getElementById('weight-from').value;
    const to = document.getElementById('weight-to').value;
    const resultDiv = document.getElementById('weight-result');
    
    if (isNaN(input)) {
        resultDiv.textContent = 'Please enter a valid number';
        return;
    }
    
    let result;
    
    // Convert to kg first
    let kg;
    switch (from) {
        case 'kg':
            kg = input;
            break;
        case 'lbs':
            kg = input / 2.20462;
            break;
        case 'grams':
            kg = input / 1000;
            break;
    }
    
    // Convert from kg to target
    switch (to) {
        case 'kg':
            result = kg;
            break;
        case 'lbs':
            result = kg * 2.20462;
            break;
        case 'ounces':
            result = kg * 35.274;
            break;
    }
    
    resultDiv.textContent = `${input} ${from} = ${result.toFixed(4)} ${to}`;
}