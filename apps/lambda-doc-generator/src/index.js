import { S3Client, PutObjectCommand } from '@aws-sdk/client-s3';
import { config } from './config.js';
import { generateRecibo } from './generators/recibo.js';
import { generateDocumentoCobranza } from './generators/documento-cobranza.js';

const s3Client = new S3Client({ region: config.region });

async function generatePdf(message) {
  const { type, data } = message;

  switch (type) {
    case 'RECIBO':
      return generateRecibo(data);

    case 'DOCUMENTO_COBRANZA':
      return generateDocumentoCobranza(data);

    default:
      throw new Error(`Tipo de documento no soportado: ${type}`);
  }
}

async function uploadToS3(pdfBuffer, message) {
  const { type, documentId } = message;
  const timestamp = Date.now();

  const prefix = config.s3Prefix.replace(/\/+$/, '');
  const typePath = type === 'DOCUMENTO_COBRANZA' ? 'documento-cobranza' : 'recibo';
  const key = `${prefix}/${typePath}/${documentId}_${timestamp}.pdf`;

  const command = new PutObjectCommand({
    Bucket: config.s3Bucket,
    Key: key,
    Body: pdfBuffer,
    ContentType: 'application/pdf'
  });

  await s3Client.send(command);
  console.log(`PDF subido a s3://${config.s3Bucket}/${key}`);
}

export const handler = async (event) => {
  const batchItemFailures = [];

  console.log(`Procesando batch de ${event.Records.length} mensaje(s)`);

  for (const record of event.Records) {
    try {
      const message = JSON.parse(record.body);
      console.log(`Generando PDF tipo=${message.type} documentId=${message.documentId}`);

      const pdfBuffer = await generatePdf(message);
      await uploadToS3(pdfBuffer, message);

      console.log(`Mensaje ${record.messageId} procesado correctamente`);
    } catch (error) {
      console.error(`Error procesando mensaje ${record.messageId}:`, error);
      batchItemFailures.push({ itemIdentifier: record.messageId });
    }
  }

  if (batchItemFailures.length > 0) {
    console.log(`${batchItemFailures.length} mensaje(s) fallido(s) de ${event.Records.length}`);
  }

  return { batchItemFailures };
};
