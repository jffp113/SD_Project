package dropbox;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import dropbox.msgs.*;
import microgram.api.java.Result;
import org.pac4j.scribe.builder.api.DropboxApi20;
import sun.net.www.http.HttpClient;
import utils.JSON;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static microgram.api.java.Result.ErrorCode.*;
import static microgram.api.java.Result.error;
import static microgram.api.java.Result.ok;

/**
 * Client for DropBox, using REST API. 
 * It allows to access the DropBox of a user.
 * 
 * Documentation for the Dropbox API can be found at:
 * 
 * https://www.dropbox.com/developers/documentation/http/documentation
 * 
 * For this code to work, there should be an app created at DropBox. You can create your own app from this URL:
 * 
 * https://www.dropbox.com/developers/apps
 * 
 */
public class DropboxClient
{
	private static final String apiKey = "dswjdrzsibbwb19";
	private static final String apiSecret = "a819o7ndldk0135";
	private static final String accessTokenStr = "TxvF2fg3FaAAAAAAAAAAYu3OBq-XU9E0EQr1-NLVt7X5By-NGnmtjvOXz25FnvbL";

	protected static final String JSON_CONTENT_TYPE = "application/json; charset=utf-8";
	protected static final String OCTETSTREAM_CONTENT_TYPE = "application/octet-stream";

	private static final String CREATE_FOLDER_V2_URL = "https://api.dropboxapi.com/2/files/create_folder_v2";
	private static final String LIST_FOLDER_V2_URL = "https://api.dropboxapi.com/2/files/list_folder";
	private static final String LIST_FOLDER_CONTINUE_V2_URL = "https://api.dropboxapi.com/2/files/list_folder/continue";
	private static final String CREATE_FILE_V2_URL = "https://content.dropboxapi.com/2/files/upload";
	private static final String DELETE_FILE_V2_URL = "https://api.dropboxapi.com/2/files/delete";
	private static final String DOWNLOAD_FILE_V2_URL = "https://content.dropboxapi.com/2/files/download";
	private static final String GET_TEMPORARY_LINK_FILE_V2_URL = "https://api.dropboxapi.com/2/files/get_temporary_link";

	private static final String DROPBOX_API_ARG = "Dropbox-API-Arg";

	protected OAuth20Service service;
	protected OAuth2AccessToken accessToken;
	
	/**
	 * Creates a dropbox client, given the access token.
	 * @throws Exception Throws exception if something failed.
	 */
	public static DropboxClient createClientWithAccessToken() throws Exception {
		return createClientWithAccessToken(accessTokenStr);
	}

	/**
	 * Creates a dropbox client, given the access token.
	 * @param accessTokenStr String with the previously obtained access token.
	 * @throws Exception Throws exception if something failed.
	 */
	public static DropboxClient createClientWithAccessToken(String accessTokenStr) throws Exception {
		try {
			OAuth20Service service = new ServiceBuilder(apiKey).apiSecret(apiSecret).build(DropboxApi20.INSTANCE);
			OAuth2AccessToken accessToken = new OAuth2AccessToken(accessTokenStr);

			System.err.println(accessToken.getAccessToken());
			System.err.println(accessToken.toString());
			return new DropboxClient( service, accessToken);

		} catch (Exception x) {
			x.printStackTrace();
			throw new Exception(x);
		}
	}

	/**
	 * Creates a dropbox client, given a file containing an access token.
	 * @param accessTokenFile File containing the previously obtained access token.
	 * @throws Exception Throws exception if something failed.
	 */
	public static DropboxClient createClientWithAccessTokenFile(File accessTokenFile) throws Exception {
		try {
			String accessTokenStr = new String(Files.readAllBytes(accessTokenFile.toPath()), StandardCharsets.UTF_8);
			return createClientWithAccessToken(accessTokenStr);
		} catch (Exception x) {
			x.printStackTrace();
			throw new Exception(x);
		}
	}

	protected DropboxClient(OAuth20Service service, OAuth2AccessToken accessToken) {
		this.service = service;
		this.accessToken = accessToken;
	}

	/**
	 * Create a directory in dropbox.
	 * 
	 * @param path
	 *            Path for the directory to create.
	 * @return Returns a Result object.
	 */
	public Result<Void> createDirectory(String path) {
		try {
			OAuthRequest createFolder = new OAuthRequest(Verb.POST, CREATE_FOLDER_V2_URL);
			createFolder.addHeader("Content-Type", JSON_CONTENT_TYPE);

			createFolder.setPayload(JSON.encode(new CreateFolderV2Args(path, false)));

			service.signRequest(accessToken, createFolder);
			Response r = service.execute(createFolder);

			if (r.getCode() == 409) {
				System.err.println("Dropbox directory already exists");
				return Result.error(Result.ErrorCode.CONFLICT);
			} else if (r.getCode() == 200) {
				System.err.println("Dropbox directory was created with success");
				return ok();
			} else {
				System.err.println("Unexpected error HTTP: " + r.getCode());
				return Result.error(Result.ErrorCode.INTERNAL_ERROR);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error(Result.ErrorCode.INTERNAL_ERROR);
		}
	}

	/**
	 * Lists diretory at Dropbox.
	 * 
	 * @param path
	 *            Dropbix path to list.
	 * @return Resturns a list of string with the contents of the directory.
	 */
	public Result<List<String>> listDirectory(String path) {
		try {
			List<String> list = new ArrayList<String>();
			OAuthRequest listFolder = new OAuthRequest(Verb.POST, LIST_FOLDER_V2_URL);
			listFolder.addHeader("Content-Type", JSON_CONTENT_TYPE);
			listFolder.setPayload(JSON.encode(new ListFolderV2Args(path, true)));

			for (;;) {
				service.signRequest(accessToken, listFolder);
				Response r = service.execute(listFolder);
				if (r.getCode() != 200) {
					System.err.println("Failed list directory: " + path + " : " + r.getMessage());
					return Result.error(Result.ErrorCode.INTERNAL_ERROR);
				}

				ListFolderV2Return result = JSON.decode(r.getBody(), ListFolderV2Return.class);
				result.getEntries().forEach(entry -> {
					list.add(entry.toString());
					System.out.println(entry);
				});

				if (result.has_more()) {
					System.err.println("continuing...");
					listFolder = new OAuthRequest(Verb.POST, LIST_FOLDER_CONTINUE_V2_URL);
					listFolder.addHeader("Content-Type", JSON_CONTENT_TYPE);
					listFolder.setPayload(JSON.encode(new ListFolderContinueV2Args(result.getCursor())));
				} else
					break;
			}
			return ok(list);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error(Result.ErrorCode.INTERNAL_ERROR);
		}
	}
	
	/**
	 * Write the contents of file name.
	 * https://www.dropbox.com/developers/documentation/http/documentation#files-upload
	 * 
	 * @param filename File name.
	 * @param bytes Contents of the file.
	 */
	public Result<String> upload(String filename, byte[] bytes) {
		Result<byte[]> result = download(filename);

		if(result.isOK()) {
			return Result.error(CONFLICT);
		}

		OAuthRequest upload = new OAuthRequest(Verb.POST, CREATE_FILE_V2_URL);
		upload.addHeader(DROPBOX_API_ARG, JSON.encode(new CreateFileV2Args(filename)));
		upload.addHeader("Content-Type", OCTETSTREAM_CONTENT_TYPE);
		upload.setPayload(bytes);

		service.signRequest(accessToken,upload);
		Response r = execute(upload);

		if(!r.isSuccessful())
		    return getError(r);

		return getUploadIdOrFail(r);
	}

	private Result<String> getUploadIdOrFail(Response r){
        try {
            return ok(JSON.decode(r.getBody(),CreateFileV2Return.class).getId());
        } catch (IOException e) {
            e.printStackTrace();
            return  error(INTERNAL_ERROR);
        }
    }

	/**
	 * Reads the contents of file name.
	 * https://www.dropbox.com/developers/documentation/http/documentation#files-download
	 * 
	 * @param filename File name.
	 * @return Returns the file contents.
	 */
	public Result<byte[]> download(String filename) {
	    OAuthRequest download = new OAuthRequest(Verb.POST, GET_TEMPORARY_LINK_FILE_V2_URL);
	    download.setPayload(JSON.encode(new AccessFileV2Args( filename)));
	    download.addHeader("Content-Type", JSON_CONTENT_TYPE);

	    service.signRequest(accessToken, download);
	    Response r = execute(download);

	    if(!r.isSuccessful())
	        return getError(r);

	    System.out.println("Start getting bytes");

	    return getBytesAsReponse(r);
	}

	private Result<byte[]> getBytesAsReponse(Response r){
        try {
            return ok(Files.readAllBytes(getImageFromLink(JSON.decode(r.getBody()
                    ,AccessFileV2Return.class).getLink()).toPath()));
        } catch (IOException e) {
            e.printStackTrace();
            return error(INTERNAL_ERROR);
        }
    }

	private File getImageFromLink(String url) {
		try {
            File f = File.createTempFile("DropBox", "image");
            f.deleteOnExit();
            InputStream in = new URL(url).openStream();
            OutputStream out = new FileOutputStream(f);

            dumpStream(in, out);

            return f;
        }catch (IOException e){
		    return null;
        }
	}

	private void dumpStream(InputStream in, OutputStream out) throws IOException {
		int b;
		while((b = in.read()) != -1){
			out.write(b);
		}
	}

	/**
	 * Deletes the file name.
	 * https://www.dropbox.com/developers/documentation/http/documentation#files-delete
	 * 
	 * @param id File name.
	 */
	public Result<Void> delete(String id) {
		OAuthRequest delete = new OAuthRequest(Verb.POST, DELETE_FILE_V2_URL);
		delete.addHeader("Content-Type", JSON_CONTENT_TYPE);
		service.signRequest(accessToken,delete);

		delete.setPayload(JSON.encode(new DeleteFileV2Args(id)));

		Response r = execute(delete);
		if(!r.isSuccessful())
		    return getError(r);


		return ok();
	}

	private Response execute(OAuthRequest request){
        try {
            return service.execute(request);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(500,"Service could not execute",null,"");
        }
    }

	private <T> Result<T> getError(Response r) {
	    System.out.println(r.getCode());
        switch (r.getCode()){
            case 409 :
            case 404 : return error(NOT_FOUND);
            case 500: return error(INTERNAL_ERROR);
        }
        return Result.error(INTERNAL_ERROR);
    }

	public static void main(String[] args) throws Exception {
	    DropboxClient.createClientWithAccessToken().delete("/text.docx");
    }

}
