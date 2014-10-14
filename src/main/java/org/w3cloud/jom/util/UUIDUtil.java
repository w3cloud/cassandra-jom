package org.w3cloud.jom.util;

public class UUIDUtil {
	/**
	 * Gets a new time uuid.
	 *
	 * @return the time uuid
	 */
	public static java.util.UUID getTimeUUID()
	{
		return java.util.UUID.fromString(new com.eaio.uuid.UUID().toString());
	}
	/**
	 * Returns an instance of uuid.
	 *
	 * @param uuid the uuid
	 * @return the java.util. uuid
	 */
	public static java.util.UUID toUUID( byte[] uuid )
	{
		long msb = 0;
		long lsb = 0;
		assert uuid.length == 16;
		for (int i=0; i<8; i++)
			msb = (msb << 8) | (uuid[i] & 0xff);
		for (int i=8; i<16; i++)
			lsb = (lsb << 8) | (uuid[i] & 0xff);
//		long mostSigBits = msb;
//		long leastSigBits = lsb;

		com.eaio.uuid.UUID u = new com.eaio.uuid.UUID(msb,lsb);
		return java.util.UUID.fromString(u.toString());
	}
	/**
	 * As byte array.
	 *
	 * @param uuid the uuid
	 *
	 * @return the byte[]
	 */
	public static byte[] asByteArray(java.util.UUID uuid)
	{
		long msb = uuid.getMostSignificantBits();
		long lsb = uuid.getLeastSignificantBits();
		byte[] buffer = new byte[16];

		for (int i = 0; i < 8; i++) {
			buffer[i] = (byte) (msb >>> 8 * (7 - i));
		}
		for (int i = 8; i < 16; i++) {
			buffer[i] = (byte) (lsb >>> 8 * (7 - i));
		}

		return buffer;
	}

}
