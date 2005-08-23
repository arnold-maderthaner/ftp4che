package org.ftp4che.event;

import java.util.EventListener;

public interface FTPListener extends EventListener{

	public void connectionStatusChanged( FTPEvent event );
	public void replyMessageArrived( FTPEvent event );
//	public void downloadStarted( FTPEvent event );
//	public void downloadFinished( FTPEvent event );
//	public void uploadStarted( FTPEvent event );
//	public void uploadFinished( FTPEvent event );
//	public void fxpStarted( FTPEvent event );
//	public void fxpFinished( FTPEvent event );
}
