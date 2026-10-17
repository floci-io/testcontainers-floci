package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.backup.BackupClient;
import software.amazon.awssdk.services.backup.model.BackupVaultListMember;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BackupServiceTest extends AbstractServiceTest {

    static BackupClient backup;

    @BeforeAll
    static void setUp() {
        backup = client(BackupClient.builder());
    }

    @Test
    void shouldListBackupVaults() {
        List<BackupVaultListMember> vaults = backup.listBackupVaults(b -> {}).backupVaultList();

        assertThat(vaults).isNotNull();
    }

    @Test
    void shouldCreateAndListBackupVault() {
        String vaultName = "test-vault-" + System.currentTimeMillis();

        backup.createBackupVault(b -> b.backupVaultName(vaultName));

        List<String> vaultNames = backup.listBackupVaults(b -> {}).backupVaultList().stream()
                .map(BackupVaultListMember::backupVaultName)
                .toList();

        assertThat(vaultNames).contains(vaultName);
    }

    @Test
    void shouldListBackupPlans() {
        var plans = backup.listBackupPlans(b -> {}).backupPlansList();

        assertThat(plans).isNotNull();
    }

}
