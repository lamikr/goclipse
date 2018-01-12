/*******************************************************************************
 * Copyright (c) 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package com.googlecode.goclipse.ui;

import com.googlecode.goclipse.tooling.env.GoEnvironmentConstants;

import melnorme.lang.ide.ui.LangImages;
import melnorme.lang.ide.ui.tools.console.LangOperationsConsoleUIHandler;
import melnorme.lang.ide.ui.tools.console.ToolsConsole;
import melnorme.lang.ide.ui.tools.console.ToolsConsole.IOConsoleOutputStreamExt;
import melnorme.lang.utils.EnvUtils;

public class GoOperationsConsoleUIHandler extends LangOperationsConsoleUIHandler {
	
	@Override
	protected ToolsConsole createBuildConsole(String name) {
		return new ToolsConsole(name, LangImages.BUILD_CONSOLE_ICON.getDescriptor()) {
			
			@Override
			protected void ui_bindActivateOnErrorsListeners() {
				// dont activate on stderr output, because Go build often uses "-v" flag
				stdErr.console().setActivateOnWrite(false);
			}
			
		};
	}
	
	@Override
	protected OperationConsoleMonitor createConsoleHandler(ProcessStartKind kind, ToolsConsole console,
			IOConsoleOutputStreamExt stdOut, IOConsoleOutputStreamExt stdErr) {
		OperationConsoleMonitor monitor = super.createConsoleHandler(kind, console, stdOut, stdErr);
		monitor.errorOnNonZeroExitValueForBuild = true;
		return monitor;
	}

	private String getEnvVarAsText(ProcessBuilder pb, String keyEnvVar) {
		String ret = EnvUtils.getVarFromEnvMap(pb.environment(), keyEnvVar);

		if ((ret != null) && (ret.length() > 0)) {
			ret = keyEnvVar + "=" + ret;
		}
		else {
			ret = "";
		}
		return ret;
	}

	private String getEnvVarAsFormattedText(ProcessBuilder pb, String keyEnvVar) {
		String ret;

		ret = getEnvVarAsText(pb, keyEnvVar);
		if ((ret != null) && (ret.length() > 0)) {
			ret = "    " + ret + "\n";
		}
		return ret;
	}

	@Override
	protected String getPrefaceText(String prefixText, String suffixText, ProcessBuilder pb) {
		String ret;

		ret = super.getPrefaceText(prefixText, suffixText, pb);
		ret = ret + getEnvVarAsFormattedText(pb, GoEnvironmentConstants.GOROOT);
		ret = ret + getEnvVarAsFormattedText(pb, GoEnvironmentConstants.GOPATH);
		ret = ret + getEnvVarAsFormattedText(pb, GoEnvironmentConstants.GOBIN);

		return ret;
	}

	@Override
	protected String getProcessTerminatedMessage(int exitCode) {
		return " " + super.getProcessTerminatedMessage(exitCode);
	};
	
}