package ziphil.module

import groovy.transform.CompileStatic
import java.security.AllPermission
import java.security.PermissionCollection
import java.security.Permissions
import java.security.Policy
import java.security.ProtectionDomain
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class AllPermissionPolicy extends Policy {

  public PermissionCollection getPermissions(ProtectionDomain domain) {
    Permissions permissions = Permissions.new()
    permissions.add(AllPermission.new())
    return permissions
  }

}