-- 令牌桶算法限流脚本
-- KEYS[1]: 限流键
-- ARGV[1]: 令牌桶容量
-- ARGV[2]: 令牌生成速率（每秒生成的令牌数）
-- ARGV[3]: 当前时间戳（秒）
-- ARGV[4]: 请求的令牌数量
-- ARGV[5]: 获取令牌的超时时间（秒）

local key = KEYS[1]
local capacity = tonumber(ARGV[1])
local rate = tonumber(ARGV[2])
local now = tonumber(ARGV[3])
local requested = tonumber(ARGV[4])
local timeout = tonumber(ARGV[5])

-- 获取当前桶的状态
local bucket = redis.call('hmget', key, 'tokens', 'last_refill_time')
local tokens = tonumber(bucket[1])   -- 当前令牌数量
local lastRefillTime = tonumber(bucket[2])  -- 上次补充令牌的时间戳

-- 初始化桶
if tokens == nil or lastRefillTime == nil then
    tokens = capacity  -- 初始化为满桶
    lastRefillTime = now  -- 设置当前时间为上次补充时间
end

-- 计算需要补充的令牌数量
local elapsed = now - lastRefillTime  -- 计算经过的时间（秒）
local refill = elapsed * rate  -- 计算应补充的令牌数量

if refill > 0 then
    tokens = math.min(capacity, tokens + refill)      -- 补充令牌，不超过容量
    lastRefillTime = now     -- 更新补充时间
end

local enough_tokens = false
-- 情况1：有足够令牌，直接扣除
if tokens >= requested then
    tokens = tokens - requested
    enough_tokens = true
-- 情况2：令牌不足但允许等待
elseif timeout > 0 then
    local need = requested - tokens   -- 计算还需要的令牌数
    local wait_time = math.ceil(need / rate)  -- 计算需要等待的时间

    if wait_time <= timeout then
        -- 等待足够的时间获取令牌
        tokens = 0      -- 消耗所有可用令牌
        lastRefillTime = now + wait_time        -- 设置未来时间作为补充时间
        enough_tokens = true
    end
end

-- 更新桶状态
redis.call('hmset', key,
    'tokens', tokens,
    'last_refill_time', lastRefillTime
)

-- 设置过期时间（避免无用的键长期存在）
-- local ttl = math.ceil(capacity / rate) * 2
local ttl = 1800
redis.call('expire', key, ttl)

-- 返回结果（1:成功, 0:失败）
if enough_tokens then
    return 1
else
    return 0
end