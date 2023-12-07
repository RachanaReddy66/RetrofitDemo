import React, { useState } from 'react';
import { View, Button, Text } from 'react-native';
import RetrofitModule from './RetrofitModule'; // Correct import path

const App = () => {
  const [responseData, setResponseData] = useState(''); // State to hold response data

  const handleRequest = async () => {
    try {
    
const xmlData = `<?xml version='1.0' encoding='UTF-8' standalone='no' ?><SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:SOAP-ENC="http://schemas.xmlsoap.org/soap/encoding/" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:ns1="http://service.fetch.rationcard/"><SOAP-ENV:Body><ns1:getPDSFpsNoDetails><VersionNo>3.1</VersionNo><deviceID>8309217739</deviceID><token>7797602c3da57f23e57a259b60358622</token><key>111</key><simID>89918680408025225797</simID><checkSum>0c9eaeea14854328f5a9f6b030518f14  </checkSum><longtude>0.000</longtude><latitude>0.000</latitude><vendorId>VISIONTEKNE</vendorId><simStatus>89918680408025225797</simStatus><macId>7C:F0:BA:F4:BA:D9</macId></ns1:getPDSFpsNoDetails></SOAP-ENV:Body></SOAP-ENV:Envelope>`;

      console.log('Sending network request...');
      RetrofitModule.makeNetworkRequest(
        'https://eposservice.jk.gov.in/ePosServiceNE3_1/', // URL
        xmlData, // XML data
        (response: string) => {
          console.log('Response:', response);
          setResponseData(response); // Set response data in state
        },
        (error: string) => {
          console.error('Error:', error);
        }
      );
    } catch (error) {
      console.error('Error:', error);
    }
  };

  return (
    <View>
      <Button title="Make Request" onPress={handleRequest} />
      {responseData ? <Text>Response: {responseData}</Text> : null}
    </View>
  );
};

export default App;
