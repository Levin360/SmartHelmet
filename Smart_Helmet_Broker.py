import serial
import time
import paho.mqtt.client as mqtt

# Initialize serial port
port = serial.Serial("/dev/ttyS0", baudrate=9600, timeout=1)


# Function to send AT command and read the response
def send_at_command(command, read_timeout=1):
    port.write(command)
    time.sleep(read_timeout)
    response = port.read(port.inWaiting())
    print(response.decode())  # Debug print to see the response
    return response


# Function to get GPS location
def get_gps_location(retries=5):
    # Turn on GPS
    send_at_command(b'AT+CGNSPWR=1\r\n')
    send_at_command(b'AT+CGNSSEQ="RMC"\r\n')
    time.sleep(2)  # Give time for GPS to power on

    for _ in range(retries):
        response = send_at_command(b'AT+CGNSINF\r\n')

        if b'+CGNSINF: ' in response:
            gps_data = response.decode().split('+CGNSINF: ')[1].split('\r\n')[0]
            gps_parts = gps_data.split(',')
            if len(gps_parts) > 4 and gps_parts[3] and gps_parts[4]:
                return gps_data
        print("Retrying to get GPS data...")
        time.sleep(2)  # Wait before retrying

    return None


# Function to get Latitude
def get_latitude(gps_data):
    if gps_data:
        gps_parts = gps_data.split(',')
        latitude = gps_parts[3]
        return f"{latitude}"
    else:
        return "Unable to retrieve latitude data."


# Function to get Longitude
def get_longitude(gps_data):
    if gps_data:
        gps_parts = gps_data.split(',')
        longitude = gps_parts[4]
        return f"{longitude}"
    else:
        return "Unable to retrieve longitude data."


# Function to perform SIM808 operations
def perform_sim808_operations():
    # Check communication with AT command
    send_at_command(b'AT\r\n')

    # Check signal strength
    response = send_at_command(b'AT+CSQ\r\n')
    if b'+CSQ:' in response:
        print("Signal strength check passed.")
    else:
        print("Signal strength check failed.")
        port.close()
        return

    # Check network registration
    response = send_at_command(b'AT+CREG?\r\n')
    if b'+CREG: 0,1' in response or b'+CREG: 0,5' in response:
        print("Network registration check passed.")
    else:
        print("Network registration check failed.")
        port.close()
        return

    # Get GPS location
    gps_data = get_gps_location()
    latitude = get_latitude(gps_data)
    longitude = get_longitude(gps_data)
    print(f"Google Maps URL: {latitude}, {longitude}")

    # Set SMS mode to text
    send_at_command(b'AT+CMGF=1\r\n')

    # Specify the phone number to send SMS
    send_at_command(b'AT+CMGS="YOUR_PHONE_NUMBER"\r\n')

    # Enter the message to send including Google Maps URL
    message = f"Accident Alert! \n \nThe Google Maps coordinate is: {latitude}, {longitude}"
    send_at_command(message.encode() + b'\r\n')

    # Send Ctrl+Z to indicate the end of the message
    port.write(bytes([26]))
    time.sleep(3)  # Increased delay to ensure message is sent

    # Dial a number (call)
    response = send_at_command(b'ATD#INSERT_THE_RECIPIENT_PHONE_NUMBER;\r\n')
    if b'OK' in response:
        print("Dialing the number...")
    else:
        print("Failed to dial the number.")

    # Wait for a while to ensure the call goes through
    time.sleep(60)  # Adjusted delay for call duration

    # End the call
    send_at_command(b'ATH\r\n')

    # Close the serial port
    port.close()


# MQTT callback functions
def on_connect(client, userdata, flags, rc):
    print("Connected with result code " + str(rc))
    client.subscribe(MQTT_TOPIC)


def on_message(client, userdata, msg):
    print(msg.topic + " " + str(msg.payload))
    if msg.payload.decode() == "Accident Detected":
        print("Accident Detected")
        perform_sim808_operations()


# MQTT connection settings
MQTT_ADDRESS = 'RASPI_IP_ADDRESS' # Enter the Raspberry Pi IP Address
MQTT_USER = 'MQTT_USERNAME'            # Enter the MQTT_USERNAME
MQTT_PASSWORD = 'MQTT_PASSWORD'        # Enter the MQTT_PASSWORD
MQTT_TOPIC = 'MQTT_TOPIC' # Enter the MQTT_TOPIC


# Main function for MQTT
def main():
    mqtt_client = mqtt.Client()
    mqtt_client.username_pw_set(MQTT_USER, MQTT_PASSWORD)
    mqtt_client.on_connect = on_connect
    mqtt_client.on_message = on_message

    mqtt_client.connect(MQTT_ADDRESS, 1883)
    mqtt_client.loop_forever()


if __name__ == '__main__':
    print('MQTT client is running')
    main()
