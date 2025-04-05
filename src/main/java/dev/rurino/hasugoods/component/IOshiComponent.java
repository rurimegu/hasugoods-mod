package dev.rurino.hasugoods.component;

import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

public interface IOshiComponent extends AutoSyncedComponent {
  String getOshiKey();

  void setOshiKey(String oshiKey);
}
