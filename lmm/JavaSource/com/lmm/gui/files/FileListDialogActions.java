package com.lmm.gui.files;

import java.io.File;
import javax.swing.JOptionPane;
import org.safehaus.uuid.UUIDGenerator;

import com.lmm.gui.GenericTableModel;
import com.lmm.msg.CmdMsg;
import com.lmm.msg.FileDeleteMsg;
import com.lmm.msg.FileRenameMsg;
import com.lmm.sched.proc.LMMUtils;


public class FileListDialogActions {

	private FileListDialog fileDialog; 
	
	protected FileListDialogActions( FileListDialog fileDialog ) {
		super();
		this.fileDialog = fileDialog;
	}

	public void deleteFiles() {
		int resp = JOptionPane.showConfirmDialog(
				fileDialog,
				"Are you sure you want to delete the " +
				fileDialog.getJTableData().getSelectedRowCount() + " selected file(s)?",
				"Confirm Deletion", JOptionPane.YES_NO_OPTION );
		
		if( resp == JOptionPane.OK_OPTION ) {
			FileDeleteMsg fdMsg = new FileDeleteMsg();
			final int[] selRows = fileDialog.getJTableData().getSelectedRows();
			for( int indx : selRows ) {
				fdMsg.getFileNames().add( 
					(String)fileDialog.getTableModel().getValueAt(indx, GenericTableModel.COL_INVIS_DATA_ID) +
					(String)fileDialog.getTableModel().getValueAt(indx, GenericTableModel.COL_UNIQUE_ID) );
			}

			if( fdMsg.getFileNames().size() > 0 ) {
				fileDialog.setNextMsgs( new FileDeleteMsg[]{fdMsg} );
			}
			
			fileDialog.setVisible(false);
		}

	}

	public void renameFile() {
		final int selRow = fileDialog.getJTableData().getSelectedRow();
		if( selRow < 0 )
			return;

		
		String fileName = 
			(String)fileDialog.getTableModel().getValueAt(selRow, GenericTableModel.COL_UNIQUE_ID);
		
		String fldrName =
			(String)fileDialog.getTableModel().getValueAt(selRow, GenericTableModel.COL_INVIS_DATA_ID);

		Object resp = JOptionPane.showInputDialog(
				fileDialog,
				"New file name",
				"Confirm Rename",
				JOptionPane.OK_CANCEL_OPTION,
				null,
				null,
				fileName );
		
		if( resp != null && ((String)resp).length() > 0 ) {
			fileDialog.setNextMsgs( new FileRenameMsg[]{
				new FileRenameMsg( fldrName + fileName, fldrName + (String)resp )} );
			
			fileDialog.setVisible(false);
		}
	}

	public void getFiles() {
		
		if( !LMMUtils.isFTPEnabled() ) {
			JOptionPane.showMessageDialog(
					fileDialog,
					"Unable to retrieve the selected file(s) since FTP is not configured",
					"Unable to retrieve file(s)",
					JOptionPane.OK_OPTION);
			
		}

		int resp = JOptionPane.showConfirmDialog(
				fileDialog,
				"Are you sure you want to transfer the selected file(s)?",
				"Confirm Transfer", JOptionPane.OK_CANCEL_OPTION );

		
		//send the correct messages
		if( resp == JOptionPane.OK_OPTION ) {

			final int[] selRows = fileDialog.getJTableData().getSelectedRows();
			CmdMsg[] cmds = new CmdMsg[selRows.length];
			for( int i = 0; i < selRows.length; i++ ) {									
				cmds[i] = new CmdMsg(CmdMsg.Commands.FTP_PUT_FILE);

				String fileId = UUIDGenerator.getInstance().generateTimeBasedUUID().toString();
				cmds[i].addHeader(CmdMsg.HDR_FILE_ID, fileId);

				//we only care about the name of the file now and not the path
				String fName =
					(String)fileDialog.getTableModel().getValueAt(selRows[i], GenericTableModel.COL_UNIQUE_ID);

				String fldrName =
					(String)fileDialog.getTableModel().getValueAt(selRows[i], GenericTableModel.COL_INVIS_DATA_ID);
				
				cmds[i].addHeader(
					CmdMsg.HDR_FILE_NAME, new File(fName).getName() );
				
				cmds[i].addHeader(
					CmdMsg.HDR_FOLDER_NAME, fldrName );

			}

			if( cmds.length > 0 )
				fileDialog.setNextMsgs( cmds );
			
			fileDialog.setVisible(false);
		}
	}

}