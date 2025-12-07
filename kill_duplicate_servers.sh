#!/bin/bash

# ===============================================
# 서버 포트 점검 및 종료 스크립트 (Bash 3.x 호환, 종료 확인 로직 포함)
# ===============================================

# 서버명과 포트를 "서버명:포트" 형태로 배열에 저장 (애플리케이션 포트 기준)
servers=(
  "Eureka_Server:8761"
  "Config_Server:8888"
  "Gateway:8080"
  "Saga_Orchestrator:8000"

  # 마이크로서비스 (Back-end Services)
  "User_Service:18080"
  "Product_Service:18090"
  "Stock_Service:18100"
  "Order_Service:18110"
  "Brand_Service:18120"
  "Payment_Service:18130"
  "Notification_Service:18140"
  "Coupon_Service:18150"
  "Review_Service:18160"
  "Cart_Service:18170"
)

echo "====== Destiny Microservice 포트 점검 및 종료 ======"

for entry in "${servers[@]}"; do
  # 서버명과 포트 분리
  name=$(echo $entry | cut -d':' -f1)ㅣ
  port=$(echo $entry | cut -d':' -f2)

  # 해당 포트를 사용하는 프로세스 ID(PID) 찾기
  pid=$(lsof -ti tcp:$port)

  if [ -n "$pid" ]; then
    echo "✅ $name (포트 $port) 실행중 PID=$pid -> 종료 시도"
    # 강제 종료 (kill -9) 및 에러 출력 무시
    kill -9 $pid 2>/dev/null

    # 1초 대기하여 시스템이 프로세스를 정리할 시간 부여
    sleep 1

    # 포트가 아직 사용 중인지 다시 확인
    remaining_pid=$(lsof -ti tcp:$port)

    if [ -z "$remaining_pid" ]; then
      echo "👍 $name (포트 $port) 프로세스 성공적으로 종료됨"
    else
      # PID가 남아있다면 종료 실패
      echo "🚨 $name (포트 $port) 프로세스 종료 실패 (남은 PID: $remaining_pid)"
    fi
  else
    echo "❌ $name (포트 $port) 프로세스 종료 완료"
  fi
done

echo "====== 모든 애플리케이션 포트 점검 및 정리 완료 ======"