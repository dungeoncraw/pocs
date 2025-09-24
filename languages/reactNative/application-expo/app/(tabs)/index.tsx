import { Image } from 'expo-image';
import { Platform, StyleSheet } from 'react-native';
import * as Application from 'expo-application';
import { useEffect, useState, useCallback } from 'react';

import ParallaxScrollView from '@/components/parallax-scroll-view';
import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';

interface ApplicationInfo {
  applicationId: string | null;
  applicationName: string | null;
  nativeApplicationVersion: string | null;
  nativeBuildVersion: string | null;
  getInstallationTimeAsync: string | null;
  getLastUpdateTimeAsync: string | null;
  getIosApplicationReleaseTypeAsync: string | null;
  nativeAndroidVersion: string | null;
  androidId: string | null;
  getIosIdForVendorAsync: string | null;
  getInstallReferrerAsync: string | null;
  getApplicationReleaseTypeAsync: string | null;
  supportsAlternateIcons: boolean;
}

export default function ApplicationInfoScreen() {
  const [appInfo, setAppInfo] = useState<ApplicationInfo | null>(null);
  const [loading, setLoading] = useState(true);

  const loadApplicationInfo = useCallback(async () => {
    try {
      const info: ApplicationInfo = {
        applicationId: Application.applicationId,
        applicationName: Application.applicationName,
        nativeApplicationVersion: Application.nativeApplicationVersion,
        nativeBuildVersion: Application.nativeBuildVersion,
        getInstallationTimeAsync: null,
        getLastUpdateTimeAsync: null,
        getIosApplicationReleaseTypeAsync: null,
        nativeAndroidVersion: null,
        androidId: null,
        getIosIdForVendorAsync: null,
        getInstallReferrerAsync: null,
        getApplicationReleaseTypeAsync: null,
        supportsAlternateIcons: false,
      };

      try {
        const installTime = await Application.getInstallationTimeAsync();
        info.getInstallationTimeAsync = installTime ? new Date(installTime).toLocaleString('en-US') : null;
      } catch (e) {
        console.log('getInstallationTimeAsync not available:', e);
      }

      try {
        const lastUpdateTime = await Application.getLastUpdateTimeAsync();
        info.getLastUpdateTimeAsync = lastUpdateTime ? new Date(lastUpdateTime).toLocaleString('en-US') : null;
      } catch (e) {
        console.log('getLastUpdateTimeAsync not available:', e);
      }

      if (Platform.OS === 'ios') {
        try {
          const releaseType = await Application.getIosApplicationReleaseTypeAsync();
          info.getIosApplicationReleaseTypeAsync = releaseType ? releaseType.toString() : null;
        } catch (e) {
          console.log('getIosApplicationReleaseTypeAsync not available:', e);
        }

        try {
          const vendorId = await Application.getIosIdForVendorAsync();
          info.getIosIdForVendorAsync = vendorId;
        } catch (e) {
          console.log('getIosIdForVendorAsync not available:', e);
        }
      }

      setAppInfo(info);
    } catch (error) {
      console.error('Error loading application information:', error);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadApplicationInfo();
  }, [loadApplicationInfo]);


  const InfoRow = ({ label, value, isAsync = false }: { label: string; value: any; isAsync?: boolean }) => (
    <ThemedView style={styles.infoRow}>
      <ThemedText type="defaultSemiBold" style={styles.label}>
        {label}{isAsync && ' (async)'}:
      </ThemedText>
      <ThemedText style={styles.value}>
        {value !== null && value !== undefined ? String(value) : 'N/A'}
      </ThemedText>
    </ThemedView>
  );

  return (
    <ParallaxScrollView
      headerBackgroundColor={{ light: '#A1CEDC', dark: '#1D3D47' }}
      headerImage={
        <Image
          source={require('@/assets/images/partial-react-logo.png')}
          style={styles.reactLogo}
        />
      }>
      <ThemedView style={styles.titleContainer}>
        <ThemedText type="title">Application Information</ThemedText>
      </ThemedView>

      <ThemedView style={styles.container}>
        <ThemedText type="subtitle" style={styles.sectionTitle}>
          Basic Information
        </ThemedText>
        {loading ? (
          <ThemedText>Loading...</ThemedText>
        ) : (
          appInfo && (
            <>
              <InfoRow label="Application ID" value={appInfo.applicationId} />
              <InfoRow label="Application Name" value={appInfo.applicationName} />
              <InfoRow label="Version" value={appInfo.nativeApplicationVersion} />
              <InfoRow label="Build Version" value={appInfo.nativeBuildVersion} />
              <InfoRow label="Platform" value={Platform.OS} />

              <ThemedText type="subtitle" style={styles.sectionTitle}>
                Installation Information
              </ThemedText>
              <InfoRow label="Installation Date" value={appInfo.getInstallationTimeAsync} isAsync />
              <InfoRow label="Last Update" value={appInfo.getLastUpdateTimeAsync} isAsync />
              <InfoRow label="Release Type" value={appInfo.getApplicationReleaseTypeAsync} isAsync />

              {Platform.OS === 'ios' && (
                <>
                  <ThemedText type="subtitle" style={styles.sectionTitle}>
                    iOS Information
                  </ThemedText>
                  <InfoRow label="iOS Release Type" value={appInfo.getIosApplicationReleaseTypeAsync} isAsync />
                  <InfoRow label="ID for Vendor" value={appInfo.getIosIdForVendorAsync} isAsync />
                  <InfoRow label="Supports Alternate Icons" value={appInfo.supportsAlternateIcons ? 'Yes' : 'No'} />
                </>
              )}

              {Platform.OS === 'android' && (
                <>
                  <ThemedText type="subtitle" style={styles.sectionTitle}>
                    Android Information
                  </ThemedText>
                  <InfoRow label="Android ID" value={appInfo.androidId} />
                  <InfoRow label="Install Referrer" value={appInfo.getInstallReferrerAsync} isAsync />
                </>
              )}

              <ThemedView style={styles.note}>
                <ThemedText style={styles.noteText}>
                  Show information about the application
                </ThemedText>
              </ThemedView>
            </>
          )
        )}
      </ThemedView>
    </ParallaxScrollView>
  );
}

const styles = StyleSheet.create({
  titleContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
    marginBottom: 20,
  },
  container: {
    flex: 1,
    gap: 12,
  },
  sectionTitle: {
    marginTop: 20,
    marginBottom: 10,
  },
  infoRow: {
    flexDirection: 'column',
    gap: 4,
    paddingVertical: 8,
    paddingHorizontal: 16,
    borderRadius: 8,
    backgroundColor: 'rgba(0,0,0,0.05)',
    marginBottom: 4,
  },
  label: {
    fontSize: 14,
    opacity: 0.8,
  },
  value: {
    fontSize: 16,
  },
  note: {
    marginTop: 24,
    padding: 16,
    borderRadius: 12,
    backgroundColor: 'rgba(0,0,0,0.02)',
  },
  noteText: {
    fontSize: 14,
    opacity: 0.7,
    lineHeight: 20,
  },
  reactLogo: {
    height: 178,
    width: 290,
    bottom: 0,
    left: 0,
    position: 'absolute',
  },
});
