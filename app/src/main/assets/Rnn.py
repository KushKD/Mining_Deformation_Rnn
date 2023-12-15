import pandas as pd
import numpy as np
import tensorflow as tf
from sklearn.preprocessing import MinMaxScaler

# Load the data
data = pd.read_csv('data.csv')
timestamps = pd.to_datetime(data['timestamp'])
inverse_velocity = data['deformation_max'].values.reshape(-1, 1)

# Normalize the data
scaler = MinMaxScaler(feature_range=(0, 1))
inverse_velocity_scaled = scaler.fit_transform(inverse_velocity)

# Prepare the data for training
X, y = [], []
for i in range(len(inverse_velocity_scaled) - 1):
    X.append(inverse_velocity_scaled[i])
    y.append(inverse_velocity_scaled[i + 1])

X, y = np.array(X), np.array(y)

# Reshape the input data for LSTM
X = np.reshape(X, (X.shape[0], 1, X.shape[1]))

# Build the LSTM model
model = tf.keras.Sequential([
    tf.keras.layers.LSTM(units=50, return_sequences=True, input_shape=(X.shape[1], X.shape[2])),
    tf.keras.layers.LSTM(units=50),
    tf.keras.layers.Dense(units=1)
])

model.compile(optimizer='adam', loss='mean_squared_error')

# Train the model
model.fit(X, y, epochs=100, batch_size=1)

# Make predictions
predicted_values_scaled = model.predict(X)

# Invert the scaling for predicted values
predicted_values = scaler.inverse_transform(predicted_values_scaled)

# Display the predictions
predictions = pd.DataFrame({
    'Timestamp': timestamps.iloc[1:],
    'Predicted_Deformation_Max': predicted_values.flatten()
})

print(predictions)
