import { useState, useEffect, useRef } from 'react';
import { BrowserMultiFormatReader } from '@zxing/library';
import Webcam from 'react-webcam';
import { Box, Button, Typography, IconButton, Dialog, DialogContent, DialogActions } from '@mui/material';
import { CameraAlt, Close } from '@mui/icons-material';

export default function BarcodeScanner({ onScan, onClose }) {
  const [devices, setDevices] = useState([]);
  const [selectedDevice, setSelectedDevice] = useState(null);
  const [result, setResult] = useState(null);
  const [error, setError] = useState(null);
  const webcamRef = useRef(null);
  const codeReader = new BrowserMultiFormatReader();

  useEffect(() => {
    const getDevices = async () => {
      try {
        const videoInputDevices = await codeReader.listVideoInputDevices();
        setDevices(videoInputDevices);
        if (videoInputDevices.length > 0) {
          setSelectedDevice(videoInputDevices[0].deviceId);
        }
      } catch (err) {
        setError('Could not access camera devices');
      }
    };

    getDevices();

    return () => {
      codeReader.reset();
    };
  }, []);

  const handleScan = () => {
    if (webcamRef.current) {
      const video = webcamRef.current.video;
      codeReader.decodeFromVideoElement(video, (result, err) => {
        if (result) {
          setResult(result.getText());
          onScan(result.getText());
          codeReader.reset();
        }
        if (err && !(err instanceof Error)) {
          setError('Barcode not found');
        }
      });
    }
  };

  const handleDeviceChange = (event) => {
    setSelectedDevice(event.target.value);
  };

  return (
    <Dialog open={true} onClose={onClose} maxWidth="md" fullWidth>
      <DialogContent>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
          <Typography variant="h6">Scan Barcode</Typography>
          <IconButton onClick={onClose}>
            <Close />
          </IconButton>
        </Box>
        
        {devices.length > 0 ? (
          <>
            <Box sx={{ mb: 2 }}>
              <select onChange={handleDeviceChange} value={selectedDevice}>
                {devices.map((device) => (
                  <option key={device.deviceId} value={device.deviceId}>
                    {device.label}
                  </option>
                ))}
              </select>
            </Box>
            
            <Box sx={{ position: 'relative', width: '100%', height: '300px', mb: 2 }}>
              <Webcam
                ref={webcamRef}
                audio={false}
                videoConstraints={{ deviceId: selectedDevice }}
                style={{ width: '100%', height: '100%', objectFit: 'cover' }}
              />
            </Box>
            
            {error && (
              <Typography color="error" sx={{ mb: 2 }}>
                {error}
              </Typography>
            )}
            
            {result && (
              <Typography sx={{ mb: 2 }}>
                Scanned: {result}
              </Typography>
            )}
          </>
        ) : (
          <Typography>No camera devices found</Typography>
        )}
      </DialogContent>
      
      <DialogActions>
        <Button
          variant="contained"
          startIcon={<CameraAlt />}
          onClick={handleScan}
          disabled={!selectedDevice}
        >
          Scan
        </Button>
      </DialogActions>
    </Dialog>
  );
}