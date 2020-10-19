// Copyright 2019 Google LLC
//
// The Play Core Native SDK is licensed to you under the Android Software
// Development Kit License Agreement -
// https://developer.android.com/studio/terms ("Agreement"). By using the Play
// Core Native SDK, you agree to the terms of this Agreement.

#ifndef PLAY_ASSET_PACK_H_
#define PLAY_ASSET_PACK_H_

#include <jni.h>
#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

/// @defgroup assetpack Play Asset Delivery
/// Native API for Play Asset Delivery
/// @{

/// An error code associated with asset pack operations.
enum AssetPackErrorCode {
  //// There was no error with the request.
  ASSET_PACK_NO_ERROR = 0,

  /// The requesting app is unavailable.
  ///
  /// This could be caused by multiple reasons:
  /// - The app isn't published in the Play Store.
  /// - The app version code isn't published in the Play Store.
  ///   Note: an older version may exist.
  /// - The user doesn't own the app, i.e. hasn't installed it in the Play
  /// Store.
  /// - The user doesn't have access to the app, e.g. alpha track.
  ASSET_PACK_APP_UNAVAILABLE = -1,

  /// The requested asset pack isn't available for this app version.
  ///
  /// This can happen if the asset pack wasn't included in the Android App
  /// Bundle that was published to the Play Store.
  ASSET_PACK_UNAVAILABLE = -2,

  /// The request is invalid.
  ASSET_PACK_INVALID_REQUEST = -3,

  /// The requested download isn't found.
  ASSET_PACK_DOWNLOAD_NOT_FOUND = -4,

  /// The Asset Pack API is unavailable.
  ASSET_PACK_API_NOT_AVAILABLE = -5,

  /// Network error. Unable to obtain asset pack details.
  ASSET_PACK_NETWORK_ERROR = -6,

  /// Download not permitted under current device circumstances, e.g. app in
  /// background or device not signed into a Google account.
  ASSET_PACK_ACCESS_DENIED = -7,

  /// Asset packs download failed due to insufficient storage.
  ASSET_PACK_INSUFFICIENT_STORAGE = -10,

  /// The Play Store app is either not installed or not the official version.
  ASSET_PACK_PLAY_STORE_NOT_FOUND = -11,

  /// Returned if showCellularDataConfirmation is called but no asset packs are
  /// waiting for Wi-Fi.
  ASSET_PACK_NETWORK_UNRESTRICTED = -12,

  /// The app is not owned by any user on this device. An app is "owned" if it
  /// has been acquired from Play.
  ASSET_PACK_APP_NOT_OWNED = -13,

  /// Unknown error downloading asset pack.
  ASSET_PACK_INTERNAL_ERROR = -100,

  /// The requested operation failed: need to call AssetPackManager_init()
  /// first.
  ASSET_PACK_INITIALIZATION_NEEDED = -101,

  /// There was an error initializing the Asset Pack API.
  ASSET_PACK_INITIALIZATION_FAILED = -102,
};

/// The status associated with asset pack download operations.
enum AssetPackDownloadStatus {
  /// Nothing is known about the asset pack. Call AssetPackManager_requestInfo()
  /// to check its size or AssetPackManager_requestDownload() start a download.
  ASSET_PACK_UNKNOWN = 0,

  /// An AssetPackManager_requestDownload() async request is pending.
  ASSET_PACK_DOWNLOAD_PENDING = 1,

  /// The asset pack download is in progress.
  ASSET_PACK_DOWNLOADING = 2,

  /// The asset pack is being transferred to the app.
  ASSET_PACK_TRANSFERRING = 3,

  /// Download and transfer are complete; the assets are available to the app.
  ASSET_PACK_DOWNLOAD_COMPLETED = 4,

  /// An AssetPackManager_requestDownload() has failed.
  ///
  /// AssetPackErrorCode will be the corresponding error, never
  /// ASSET_PACK_NO_ERROR. The bytes_downloaded and total_bytes_to_download
  /// fields are unreliable given this status.
  ASSET_PACK_DOWNLOAD_FAILED = 5,

  /// Asset pack download has been canceled.
  ASSET_PACK_DOWNLOAD_CANCELED = 6,

  /// The asset pack download is waiting for Wi-Fi to proceed.
  ///
  /// Optionally, call AssetPackManager_showCellularDataConfirmation() to ask
  /// the user to confirm downloading over cellular data.
  ASSET_PACK_WAITING_FOR_WIFI = 7,

  /// The asset pack isn't installed.
  ASSET_PACK_NOT_INSTALLED = 8,

  /// An AssetPackManager_requestInfo() async request started, but the result
  /// isn't known yet.
  ASSET_PACK_INFO_PENDING = 100,

  /// An AssetPackManager_requestInfo() async request has failed.
  ///
  /// AssetPackErrorCode will be the corresponding error, never
  /// ASSET_PACK_NO_ERROR. The bytes_downloaded and total_bytes_to_download
  /// fields are unreliable given this status.
  ASSET_PACK_INFO_FAILED = 101,

  /// An AssetPackManager_requestRemoval() async request started.
  ASSET_PACK_REMOVAL_PENDING = 110,

  /// An AssetPackManager_requestRemoval() async request has failed.
  ASSET_PACK_REMOVAL_FAILED = 111,
};

/// The method used to store an asset pack on the device.
enum AssetPackStorageMethod {
  /// The asset pack is unpacked into a folder containing individual asset
  /// files.
  ///
  /// Assets can be accessed via standard File APIs.
  ASSET_PACK_STORAGE_FILES = 0,

  /// The asset pack is installed as an APK containing packed asset files.
  ///
  /// Assets can be accessed via AAssetManager.
  ASSET_PACK_STORAGE_APK = 1,

  /// Nothing is known, perhaps due to an error.
  ASSET_PACK_STORAGE_UNKNOWN = 100,

  /// The asset pack is not installed.
  ASSET_PACK_STORAGE_NOT_INSTALLED = 101,
};

/// The status associated with a request to display a cellular data confirmation
/// dialog.
enum ShowCellularDataConfirmationStatus {
  /// AssetPackManager_showCellularDataConfirmation() has not been called.
  ASSET_PACK_CONFIRM_UNKNOWN = 0,

  /// AssetPackManager_showCellularDataConfirmation() has been called, but the
  /// user hasn't made a choice.
  ASSET_PACK_CONFIRM_PENDING = 1,

  /// The user approved of downloading asset packs over cellular data.
  ASSET_PACK_CONFIRM_USER_APPROVED = 2,

  /// The user declined to download asset packs over cellular data.
  ASSET_PACK_CONFIRM_USER_CANCELED = 3,
};

/// An opaque struct used to access the state of an individual asset pack
/// including download status and download size.
typedef struct AssetPackDownloadState_ AssetPackDownloadState;

/// An opaque struct used to access how and where an asset pack's assets
/// are stored on the device.
typedef struct AssetPackLocation_ AssetPackLocation;

/// Initialize the Asset Pack API, making the other functions available to call.
///
/// In case of failure the Asset Pack API is unavailable, and there will be an
/// error in logcat. The most common reason for failure is that the PlayCore AAR
/// is missing or some of its classes/methods weren't retained by ProGuard.
/// @param jvm The app's single JavaVM, e.g. from ANativeActivity's "vm" field.
/// @param android_context An Android Context, e.g. from ANativeActivity's
/// "clazz" field.
/// @return ASSET_PACK_NO_ERROR if initialization succeeded.
/// @see AssetPackManager_destroy
AssetPackErrorCode AssetPackManager_init(JavaVM* jvm, jobject android_context);

/// Frees up memory allocated for the Asset Pack API.
///
/// Does nothing if AssetPackManager_init() hasn't been called.
void AssetPackManager_destroy();

/// Should be called in ANativeActivity ANativeActivityCallbacks's onResume.
/// Internally, this registers a state update listener.
/// @return ASSET_PACK_NO_ERROR if the call is successful.
AssetPackErrorCode AssetPackManager_onResume();

/// Should be called in ANativeActivity ANativeActivityCallbacks's onPause.
/// Internally, this deregisters a state update listener.
/// @return ASSET_PACK_NO_ERROR if the call is successful.
AssetPackErrorCode AssetPackManager_onPause();

/// Asynchronously requests download info about the specified asset packs. Use
/// AssetPackManager_getDownloadState() to monitor progress and get the result.
/// @param asset_packs An array of asset pack names.
/// @param num_asset_packs The length of the asset_packs array.
/// @return ASSET_PACK_NO_ERROR if the request started successfully.
AssetPackErrorCode AssetPackManager_requestInfo(const char** asset_packs,
                                                size_t num_asset_packs);

/// Asynchronously requests to start downloading the specified asset packs.
/// Use AssetPackManager_getDownloadState() to monitor download progress.
/// @param asset_packs An array of asset pack names.
/// @param num_asset_packs The length of the asset_packs array.
/// @return ASSET_PACK_NO_ERROR if the request started successfully.
AssetPackErrorCode AssetPackManager_requestDownload(const char** asset_packs,
                                                    size_t num_asset_packs);

/// Cancels downloading the specified asset packs.
///
/// Note: Only active downloads will be canceled. Asset packs with a status of
/// ASSET_PACK_DOWNLOAD_COMPLETED or ASSET_PACK_NOT_INSTALLED are unaffected by
/// this method.
/// @param asset_packs An array of asset pack names.
/// @param num_asset_packs The length of the asset_packs array.
/// @return Always ASSET_PACK_NO_ERROR, except in the case of an invalid call,
/// e.g. ASSET_PACK_INITIALIZATION_NEEDED or ASSET_PACK_INVALID_REQUEST.
AssetPackErrorCode AssetPackManager_cancelDownload(const char** asset_packs,
                                                   size_t num_asset_packs);

/// Asynchronously requests to delete the specified asset pack from internal
/// storage. Use AssetPackManager_getDownloadState() to check deletion progress.
///
/// Use this function to delete asset packs instead of deleting the files
/// manually. This ensures that the asset pack won't be re-downloaded during an
/// app update.
///
/// If the asset pack is currently being downloaded or installed, this function
/// won't cancel that operation.
/// @param name An asset pack name.
/// @return ASSET_PACK_NO_ERROR if the request started successfully.
AssetPackErrorCode AssetPackManager_requestRemoval(const char* name);

/// Gets the download state of the specified asset pack.
///
/// The last known error code is returned directly, whereas other state can be
/// obtained via the AssetPackDownloadState out parameter by using functions
/// such as AssetPackDownloadState_getStatus().
///
/// This function can be used to monitor progress or get the final result of a
/// call to AssetPackManager_requestInfo(), AssetPackManager_requestDownload(),
/// or AssetPackManager_requestRemoval(). This method does not make any JNI
/// calls and can be called every frame.
/// @param name An asset pack name.
/// @param out_state An out parameter for receiving the result.
/// @return ASSET_PACK_NO_ERROR if the request started successfully.
/// @see AssetPackDownloadState_destroy
AssetPackErrorCode AssetPackManager_getDownloadState(
    const char* name, AssetPackDownloadState** out_state);

/// Releases the specified AssetPackDownloadState and any references it holds.
/// @param state The state to free.
void AssetPackDownloadState_destroy(AssetPackDownloadState* state);

/// Gets the AssetPackDownloadStatus for the specified AssetPackDownloadState.
/// @param state The state for which to get status.
/// @return The AssetPackDownloadStatus.
AssetPackDownloadStatus AssetPackDownloadState_getStatus(
    AssetPackDownloadState* state);

/// Gets the total number of bytes already downloaded for the asset pack
/// associated with the specified AssetPackDownloadState.
/// @param state The state for which to get bytes downloaded.
/// @return The total number of bytes already downloaded.
uint64_t AssetPackDownloadState_getBytesDownloaded(
    AssetPackDownloadState* state);

/// Gets the total size in bytes for the asset pack associated with the
/// specified AssetPackDownloadState.
/// @param state The state for which to get total bytes to download.
/// @return The total size in bytes for the asset pack.
uint64_t AssetPackDownloadState_getTotalBytesToDownload(
    AssetPackDownloadState* state);

/// Shows a confirmation dialog to resume all asset pack downloads that are
/// currently in the ASSET_PACK_WAITING_FOR_WIFI state. If the user agrees to
/// the dialog prompt, asset packs are downloaded over cellular data.
//
/// The status of an asset pack is set to ASSET_PACK_WAITING_FOR_WIFI if the
/// user is currently not on a Wi-Fi connection and the asset pack is large or
/// the user has set their download preference in the Play Store to only
/// download apps over Wi-Fi. By showing this dialog, your app can ask the user
/// if they accept downloading the asset pack over cellular data instead of
/// waiting for Wi-Fi.
/// @param android_activity An Android Activity, e.g. from ANativeActivity's
/// "clazz" field.
/// @return ASSET_PACK_NO_ERROR if the dialog is shown. Call
/// AssetPackManager_getShowCellularDataConfirmationStatus() to get the dialog
/// result.
AssetPackErrorCode AssetPackManager_showCellularDataConfirmation(
    jobject android_activity);

/// Gets the status of AssetPackManager_showCellularDataConfirmation() requests.
///
/// This function does not make any JNI calls and can be called every frame.
/// @param out_status An out parameter for receiving the result.
/// @return An AssetPackErrorCode, which if not ASSET_PACK_NO_ERROR indicates
/// that the out parameter should not be used.
AssetPackErrorCode AssetPackManager_getShowCellularDataConfirmationStatus(
    ShowCellularDataConfirmationStatus* out_status);

/// Obtains an AssetPackLocation for the specified asset pack that can be used
/// to query how and where the asset pack's assets are stored on the device.
/// @param name An asset pack name.
/// @param out_location An out parameter for receiving the result.
/// @return An AssetPackErrorCode, which if not ASSET_PACK_NO_ERROR indicates
/// that the out parameter shouldn't be used.
/// @see AssetPackLocation_destroy
AssetPackErrorCode AssetPackManager_getAssetPackLocation(
    const char* name, AssetPackLocation** out_location);

/// Releases the specified AssetPackLocation and any references it holds.
/// @param location The location to free.
void AssetPackLocation_destroy(AssetPackLocation* location);

/// Gets the AssetPackStorageMethod for the specified asset pack.
/// @param location The location for which to get a storage method.
/// @return The AssetPackStorageMethod for the specified asset pack.
AssetPackStorageMethod AssetPackLocation_getStorageMethod(
    AssetPackLocation* location);

/// Gets a file path to the directory containing the asset pack's unpackaged
/// assets.
///
/// The files found in this path should not be modified.
///
/// The string returned here is owned by the AssetPackManager implementation and
/// will be freed by calling AssetPackLocation_destroy().
/// @param location The location from which to obtain the assets path.
/// @return A file path to the directory containing the asset pack's unpackaged
/// assets, provided that the asset pack's storage method is
/// ASSET_PACK_STORAGE_FILES. Otherwise, returns a null pointer.
const char* AssetPackLocation_getAssetsPath(AssetPackLocation* location);

/// @}

#ifdef __cplusplus
};  // extern "C"
#endif

#endif  // PLAY_ASSET_PACK_H_
