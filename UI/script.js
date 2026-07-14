const fromUnit = document.getElementById('fromUnit');
const toUnit = document.getElementById('toUnit');
const inputValue = document.getElementById('inputValue');
const convertBtn = document.getElementById('convertBtn');
const resultBox = document.getElementById('resultBox');
const errorMsg = document.getElementById('errorMsg');

const UNIT_SYMBOL = { C: '°C', F: '°F', K: 'K' };

function showError(msg) {
  errorMsg.textContent = msg;
  errorMsg.hidden = false;
  resultBox.textContent = '';
}

function hideError() {
  errorMsg.hidden = true;
}

async function handleConvert() {
  const raw = inputValue.value.trim();

  if (raw === '') {
    showError('Please enter a temperature');
    return;
  }

  const value = Number(raw);
  if (Number.isNaN(value)) {
    showError('Temperature must be a number');
    return;
  }

  const from = fromUnit.value;
  const to = toUnit.value;

  convertBtn.disabled = true;
  convertBtn.textContent = 'Converting...';

  try {
    const res = await fetch(`/api/convert?value=${value}&from=${from}&to=${to}`, {
      method: 'POST',
    });
    const data = await res.json();

    if (!res.ok) {
      showError(data.error || data.message || 'Conversion failed');
      return;
    }

    hideError();
    resultBox.textContent = `${data.outputTemperature} ${UNIT_SYMBOL[data.outputUnit]}`;
  } catch (err) {
    showError('Could not reach the server');
  } finally {
    convertBtn.disabled = false;
    convertBtn.textContent = 'Convert';
  }
}

convertBtn.addEventListener('click', handleConvert);

// Allow pressing Enter in the input field to trigger conversion too
inputValue.addEventListener('keydown', (e) => {
  if (e.key === 'Enter') handleConvert();
});
